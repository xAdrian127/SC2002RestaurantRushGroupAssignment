package control;

import entity.HappyHour;
import entity.Table;
import entity.customer.Customer;
import entity.queue.PaymentQueue;
import entity.queue.WaitingQueue;
import entity.staff.Cashier;
import entity.staff.Chef;
import entity.staff.Host;
import entity.staff.Waiter;

import java.util.List;

/**
 * Top-level Turn Orchestrator and Facade (Controller Pattern).
 * Coordinates specialized sub-controllers adhering to the Single Responsibility Principle (SRP):
 * - StaffController: Manages staff tasks (Host, Waiter, Chef, Cashier) and action quotas.
 * - CustomerController: Manages customer arrivals, eating progression, Happy Hour recovery, and decay/abandonment.
 * - GameScoreManager: Manages revenue, paid customer counts, satisfaction averages, and win/loss criteria.
 * - HappyHour: Manages the dynamic pricing policy and turn countdown (Section 4.3).
 */
public class RestaurantController {
    private final Table[] tables = new Table[2];
    private final WaitingQueue waitingQueue = new WaitingQueue();
    private final PaymentQueue paymentQueue = new PaymentQueue();

    // Specialized SRP Sub-Controllers
    private final StaffController staffController = new StaffController();
    private final CustomerController customerController = new CustomerController();
    private final GameScoreManager scoreManager = new GameScoreManager();

    // Stage 3 Dynamic Pricing Policy
    private final HappyHour happyHour = new HappyHour();

    private int currentTurn = 1;

    public RestaurantController() {
        for (int i = 0; i < tables.length; i++) {
            tables[i] = new Table(i + 1);
        }
    }

    // ==========================================
    // TURN LIFECYCLE ENGINE
    // ==========================================

    /**
     * Executes Step 1 (Start of Turn):
     * 1. Resets staff action flags for the new turn.
     * 2. Displays turn header and active Happy Hour policy banner.
     * 3. Automated Cashier Payment (oldest first, at most 1 per turn, freeing tables).
     * 4. Automatic State Progression: Customers who finished eating become READY_TO_PAY.
     * 5. Scheduled customer arrivals.
     * 6. Happy Hour satisfaction recovery for waiting/unserved customers (+15) if active from prior turn.
     * 7. Displays live metrics.
     */
    public void processTurnStart(int turn_num) {
        this.currentTurn = turn_num;

        // 1. Reset staff turns for the new turn
        staffController.resetStaffTurns();

        // 2. Clear Turn Header establishing the START of the turn
        System.out.println("\n==========================================");
        System.out.println(">>> TURN " + turn_num + " of 18 <<<");
        if (happyHour.isActive()) {
            System.out.println("[POLICY] Happy Hour is ACTIVE! Remaining Turns: "
                    + happyHour.getTurnsRemaining() + " | 20% discount on new orders");
        }
        System.out.println("==========================================");

        // 3. Automated Cashier Payment at turn start (FIFO oldest-first, freeing tables immediately)
        staffController.processCashierPayment(paymentQueue, scoreManager, currentTurn);

        // 4. Automatic State Progression: Customers who finished eating become READY_TO_PAY
        customerController.processEatingProgression(tables, paymentQueue, turn_num);

        // 5. Scheduled arrivals (Turns 1, 4, 7, 10, 13, 16)
        customerController.processArrivals(turn_num, waitingQueue);

        // 6. If Happy Hour was already active from a previous turn, apply turn-start recovery (+15)
        if (happyHour.isActive()) {
            customerController.processHappyHourRecovery(waitingQueue, tables);
        }

        // 7. Display live metrics after start-of-turn payment & arrivals
        System.out.println("Revenue: $" + String.format("%.2f", scoreManager.getTotalRevenue())
                + " | Paid: " + scoreManager.getPaidCustomerCount()
                + " | Avg Sat: " + scoreManager.getAverageSatisfactionString());
    }

    /**
     * Executes Step 3-5 (End of Turn):
     * Performs satisfaction decay, handles customer abandonment, and decrements Happy Hour duration.
     */
    public void processTurnEnd() {
        customerController.processDecayAndAbandonment(waitingQueue, tables, paymentQueue, scoreManager);
        happyHour.endTurn();
    }

    // ==========================================
    // HAPPY HOUR POLICY ACTIONS
    // ==========================================

    /**
     * Activates Happy Hour at the start of a turn.
     * Triggers turn-start satisfaction recovery (+15) immediately for eligible customers.
     *
     * @return true if activation succeeded, false if already activated previously.
     */
    public boolean activateHappyHour() {
        boolean ok = happyHour.activate();
        if (ok) {
            System.out.println("[ACTION] Happy Hour successfully activated for 2 turns!");
            // Immediate turn-start satisfaction recovery for eligible customers for the activation turn
            customerController.processHappyHourRecovery(waitingQueue, tables);
        } else {
            System.out.println("[DENIED] Happy Hour has already been activated once this game.");
        }
        return ok;
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
    // HOST ACTIONS (Stage 3)
    // ==========================================

    public boolean hostSeatCustomer(int tableIndex) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice (must be 1 to " + tables.length + ").");
            return false;
        }
        return staffController.hostSeatCustomer(waitingQueue, tables[tableIndex]);
    }

    public void hostWait() {
        staffController.hostWait();
    }

    public Host getHost() {
        return staffController.getHost();
    }

    public boolean hasHostActed() {
        return staffController.hasHostActed();
    }

    // ==========================================
    // WAITER & CHEF ACTIONS
    // ==========================================

    public boolean seatCustomer(int tableIndex) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice (must be 1 to " + tables.length + ").");
            return false;
        }
        return staffController.seatCustomer(waitingQueue, tables[tableIndex]);
    }

    public boolean takeOrder(int tableIndex) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        return staffController.takeOrder(tables[tableIndex], happyHour.isActive());
    }

    public boolean takeOrder(int tableIndex, int menuChoice) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        return staffController.takeOrder(tables[tableIndex], menuChoice, happyHour.isActive());
    }

    public boolean serveFood(int tableIndex) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        return staffController.serveFood(tables[tableIndex], scoreManager);
    }

    public void waiterWait() {
        staffController.waiterWait();
    }

    public boolean cookDish(int tableIndex) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice for Chef.");
            return false;
        }
        return staffController.cookDish(tables[tableIndex]);
    }

    public void chefWait() {
        staffController.chefWait();
    }

    // ==========================================
    // ACCESSORS
    // ==========================================

    public StaffController getStaffController() {
        return staffController;
    }

    public CustomerController getCustomerController() {
        return customerController;
    }

    public GameScoreManager getScoreManager() {
        return scoreManager;
    }

    public Waiter getWaiter() {
        return staffController.getWaiter();
    }

    public Chef getChef() {
        return staffController.getChef();
    }

    public Cashier getCashier() {
        return staffController.getCashier();
    }

    public boolean hasWaiterActed() {
        return staffController.hasWaiterActed();
    }

    public boolean hasChefActed() {
        return staffController.hasChefActed();
    }

    public int getCurrentTurn() {
        return currentTurn;
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

    public Table[] getTables() {
        return tables;
    }

    public List<Customer> getWaitingQueue() {
        return waitingQueue.getCustomers();
    }

    public WaitingQueue getWaitingQueueAsQueue() {
        return waitingQueue;
    }

    public PaymentQueue getPaymentQueue() {
        return paymentQueue;
    }

    public boolean isGameOver() {
        return currentTurn > 18;
    }
}
