package control;

import entity.Table;
import entity.customer.Customer;
import entity.queue.PaymentQueue;
import entity.queue.WaitingQueue;
import entity.staff.Cashier;
import entity.staff.Chef;
import entity.staff.Waiter;

import java.util.List;

/**
 * Top-level Turn Orchestrator and Facade (Controller Pattern).
 * Coordinates specialized sub-controllers adhering to the Single Responsibility Principle (SRP):
 * - StaffController: Manages staff tasks and action dispatching.
 * - CustomerController: Manages customer arrivals, eating progression, and decay/abandonment.
 * - GameScoreManager: Manages revenue, paid customer counts, satisfaction averages, and win/loss criteria.
 */
public class RestaurantController {
    private final Table[] tables = new Table[2];
    private final WaitingQueue waitingQueue = new WaitingQueue();
    private final PaymentQueue paymentQueue = new PaymentQueue();

    // Specialized SRP Sub-Controllers
    private final StaffController staffController = new StaffController();
    private final CustomerController customerController = new CustomerController();
    private final GameScoreManager scoreManager = new GameScoreManager();

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
     * 2. Automated Cashier Payment (oldest first, at most 1 per turn).
     * 3. Displays turn header and live metrics.
     * 4. Scheduled customer arrivals.
     * 5. Advances served customers into READY_TO_PAY.
     */
    public void processTurnStart(int turn_num) {
        this.currentTurn = turn_num;

        // 1. Reset staff turns for the new turn
        staffController.resetStaffTurns();

        // 2. Clear Turn Header establishing the START of the turn
        System.out.println("\n==========================================");
        System.out.println(">>> TURN " + turn_num + " of 18 <<<");
        System.out.println("==========================================");

        // 3. Automated Cashier Payment at turn start (FIFO oldest-first, freeing tables immediately)
        // Customers who became READY_TO_PAY on an earlier turn are paid and vacate their tables now.
        staffController.processCashierPayment(paymentQueue, scoreManager, currentTurn);

        // 4. Automatic State Progression: Customers who finished eating become READY_TO_PAY
        customerController.processEatingProgression(tables, paymentQueue, turn_num);

        // 5. Scheduled arrivals (Turns 1, 4, 7, 10, 13, 16)
        customerController.processArrivals(turn_num, waitingQueue);

        // 6. Display live metrics after start-of-turn payment & arrivals
        System.out.println("Revenue: $" + String.format("%.2f", scoreManager.getTotalRevenue())
                + " | Paid: " + scoreManager.getPaidCustomerCount()
                + " | Avg Sat: " + scoreManager.getAverageSatisfactionString());
    }

    /**
     * Executes Step 3-5 (End of Turn):
     * Performs satisfaction decay and handles customer abandonment.
     */
    public void processTurnEnd() {
        customerController.processDecayAndAbandonment(waitingQueue, tables, paymentQueue, scoreManager);
    }

    // ==========================================
    // STAFF ACTION DELEGATION
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
        return staffController.takeOrder(tables[tableIndex]);
    }

    public boolean takeOrder(int tableIndex, int menuChoice) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        return staffController.takeOrder(tables[tableIndex], menuChoice);
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
    // CONTROLLER & ENTITY ACCESSORS
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

    // ==========================================
    // METRICS & STATE ACCESSORS (For UI Layer)
    // ==========================================

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

    public PaymentQueue getPaymentQueue() {
        return paymentQueue;
    }

    public boolean isGameOver() {
        return currentTurn > 18;
    }
}
