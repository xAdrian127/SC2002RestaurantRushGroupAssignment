package control;

import entity.HappyHour;
import entity.Table;
import entity.customer.Customer;
import entity.queue.PaymentQueue;
import entity.queue.WaitingQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Top-level Turn Orchestrator and Facade (Controller Pattern).
 * Coordinates specialized sub-controllers adhering to the Single Responsibility Principle (SRP)
 * and strict Encapsulation without performing console I/O:
 * - StaffController: Manages staff tasks (Host, Waiter, Chef, Cashier) and action quotas.
 * - CustomerController: Manages customer arrivals, eating progression, Happy Hour recovery, and decay/abandonment.
 * - GameScoreManager: Manages revenue, paid customer counts, satisfaction averages, and win/loss criteria.
 * - HappyHour: Manages the dynamic pricing policy and turn countdown (Section 4.3).
 */
public class RestaurantController {
    public static final int MAX_TURNS = 18;
    public static final int TABLE_COUNT = 2;

    private final List<Table> tables;
    private final WaitingQueue waitingQueue = new WaitingQueue();
    private final PaymentQueue paymentQueue = new PaymentQueue();

    private final StaffController staffController = new StaffController();
    private final CustomerController customerController = new CustomerController();
    private final GameScoreManager scoreManager = new GameScoreManager();
    private final HappyHour happyHour = new HappyHour();

    private int currentTurn = 1;

    public RestaurantController() {
        List<Table> initializedTables = new ArrayList<>(TABLE_COUNT);
        for (int i = 1; i <= TABLE_COUNT; i++) {
            initializedTables.add(new Table(i));
        }
        this.tables = Collections.unmodifiableList(initializedTables);
    }

    // ==========================================
    // TURN LIFECYCLE ENGINE
    // ==========================================

    /**
     * Executes Step 1 (Start of Turn) domain transitions and returns structured event logs:
     * 1. Resets staff action flags for the new turn.
     * 2. Automated Cashier Payment (oldest first, at most 1 per turn, freeing tables).
     * 3. Automatic State Progression: Customers who finished eating become READY_TO_PAY.
     * 4. Scheduled customer arrivals.
     * 5. Happy Hour satisfaction recovery for waiting/unserved customers (+15) if active from prior turn.
     */
    public List<String> processTurnStart(int turnNumber) {
        this.currentTurn = turnNumber;
        List<String> events = new ArrayList<>();

        staffController.resetStaffTurns();
        staffController.processCashierPayment(paymentQueue, scoreManager, currentTurn, events);
        customerController.processEatingProgression(tables, paymentQueue, turnNumber, events);
        customerController.processArrivals(turnNumber, waitingQueue, events);

        if (happyHour.isActive()) {
            customerController.processHappyHourRecovery(waitingQueue, tables, events);
        }

        return Collections.unmodifiableList(events);
    }

    /**
     * Executes Steps 3-5 (End of Turn):
     * Performs satisfaction decay, handles customer abandonment, decrements Happy Hour duration,
     * and returns structured abandonment event logs.
     */
    public List<String> processTurnEnd() {
        List<String> events = new ArrayList<>();
        customerController.processDecayAndAbandonment(waitingQueue, tables, paymentQueue, scoreManager, events);
        happyHour.endTurn();
        return Collections.unmodifiableList(events);
    }

    // ==========================================
    // HAPPY HOUR POLICY ACTIONS
    // ==========================================

    /**
     * Activates Happy Hour at the start of a turn (once per game).
     * Triggers turn-start satisfaction recovery (+15) immediately for eligible customers.
     */
    public ActionResult activateHappyHour() {
        if (!happyHour.activate()) {
            return ActionResult.denied("[DENIED] Happy Hour has already been activated once this game.");
        }
        List<String> messages = new ArrayList<>();
        messages.add("[ACTION] Happy Hour successfully activated for 2 turns!");
        customerController.processHappyHourRecovery(waitingQueue, tables, messages);
        return ActionResult.success(messages);
    }

    public boolean isHappyHourActive() {
        return happyHour.isActive();
    }

    public boolean isHappyHourActivated() {
        return happyHour.isActivated();
    }

    public int getHappyHourTurnsRemaining() {
        return happyHour.getTurnsRemaining();
    }

    // ==========================================
    // HOST, WAITER & CHEF FACADE COMMANDS
    // ==========================================

    private Table resolveTable(int tableNumber) {
        if (tableNumber < 1 || tableNumber > tables.size()) {
            return null;
        }
        return tables.get(tableNumber - 1);
    }

    public ActionResult hostSeatCustomer(int tableNumber) {
        Table table = resolveTable(tableNumber);
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice (must be 1 to " + tables.size() + ").");
        }
        return staffController.hostSeatCustomer(waitingQueue, table);
    }

    public ActionResult hostWait() {
        return staffController.hostWait();
    }

    public ActionResult seatCustomer(int tableNumber) {
        Table table = resolveTable(tableNumber);
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice (must be 1 to " + tables.size() + ").");
        }
        return staffController.seatCustomer(waitingQueue, table);
    }

    public ActionResult takeOrder(int tableNumber) {
        Table table = resolveTable(tableNumber);
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice (must be 1 to " + tables.size() + ").");
        }
        return staffController.takeOrder(table, happyHour.isActive());
    }

    public ActionResult serveFood(int tableNumber) {
        Table table = resolveTable(tableNumber);
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice (must be 1 to " + tables.size() + ").");
        }
        return staffController.serveFood(table, scoreManager);
    }

    public ActionResult waiterWait() {
        return staffController.waiterWait();
    }

    public ActionResult cookDish(int tableNumber) {
        Table table = resolveTable(tableNumber);
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice for Chef.");
        }
        return staffController.cookDish(table);
    }

    public ActionResult chefWait() {
        return staffController.chefWait();
    }

    // ==========================================
    // READ-ONLY QUERIES FOR BOUNDARY LAYER
    // ==========================================

    public int getMaxTurns() {
        return MAX_TURNS;
    }

    public int getTableCount() {
        return tables.size();
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Table> getCookableTables() {
        List<Table> cookable = new ArrayList<>();
        for (Table table : tables) {
            if (table.hasCookableOrder()) {
                cookable.add(table);
            }
        }
        return Collections.unmodifiableList(cookable);
    }

    public List<Customer> getWaitingQueue() {
        return waitingQueue.getCustomers();
    }

    public boolean isWaitingQueueEmpty() {
        return waitingQueue.isEmpty();
    }

    public double getTotalRevenue() {
        return scoreManager.getTotalRevenue();
    }

    public int getTotalRevenueCents() {
        return scoreManager.getTotalRevenueCents();
    }

    public int getPaidCustomerCount() {
        return scoreManager.getPaidCustomerCount();
    }

    public int getServedCustomerCount() {
        return scoreManager.getServedCustomerCount();
    }

    public int getUnhappyDepartures() {
        return scoreManager.getUnhappyDepartures();
    }

    public double getAverageSatisfactionNumeric() {
        return scoreManager.getAverageSatisfactionNumeric();
    }

    public String getAverageSatisfactionString() {
        return scoreManager.getAverageSatisfactionString();
    }

    public boolean isTargetCustomersMet() {
        return scoreManager.isTargetCustomersMet();
    }

    public boolean isTargetRevenueMet() {
        return scoreManager.isTargetRevenueMet();
    }

    public boolean isTargetSatisfactionMet() {
        return scoreManager.isTargetSatisfactionMet();
    }

    public boolean isVictory() {
        return scoreManager.isVictory();
    }
}
