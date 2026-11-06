package control;

import entity.Table;
import entity.customer.Customer;
import entity.queue.PaymentQueue;
import entity.queue.WaitingQueue;
import entity.staff.Cashier;
import entity.staff.Chef;
import entity.staff.Host;
import entity.staff.Waiter;

/**
 * Manages staff coordination and action dispatching (Single Responsibility Principle).
 * Encapsulates Waiter tasks, Host seating, Chef cooking, Cashier billing, and turn action rules.
 */
public class StaffController {
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();
    private final Host host = new Host();

    /**
     * Resets action flags for all staff members at the start of a turn.
     */
    public void resetStaffTurns() {
        waiter.resetTurn();
        chef.resetTurn();
        cashier.resetTurn();
        host.resetTurn();
    }

    // ==========================================
    // HOST ACTIONS (Stage 3)
    // ==========================================

    public boolean hostSeatCustomer(WaitingQueue waitingQueue, Table table) {
        if (table == null) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        if (waitingQueue.isEmpty()) {
            System.out.println("[DENIED] Cannot seat customer: waiting queue is empty.");
            return false;
        }
        if (table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is already occupied.");
            return false;
        }
        Customer c = waitingQueue.removeFirst();
        boolean seated = host.seat(c, table);
        if (!seated) {
            waitingQueue.add(c); // Restore customer if seating failed
            return false;
        }
        return true;
    }

    public void hostWait() {
        host.waitTurn();
    }

    public Host getHost() {
        return host;
    }

    public boolean hasHostActed() {
        return host.hasActed();
    }

    // ==========================================
    // WAITER ACTIONS
    // ==========================================

    public boolean seatCustomer(WaitingQueue waitingQueue, Table table) {
        if (table == null) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        if (waitingQueue.isEmpty()) {
            System.out.println("[DENIED] Cannot seat customer: waiting queue is empty.");
            return false;
        }
        if (table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is already occupied.");
            return false;
        }
        Customer c = waitingQueue.removeFirst();
        boolean seated = waiter.seat(c, table);
        if (!seated) {
            waitingQueue.add(c); // Restore customer if seating failed
            return false;
        }
        return true;
    }

    public boolean takeOrder(Table table, boolean isHappyHourActive) {
        if (table == null) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        return waiter.takeOrder(table, isHappyHourActive);
    }

    public boolean takeOrder(Table table) {
        return takeOrder(table, false);
    }

    public boolean takeOrder(Table table, int menuChoice, boolean isHappyHourActive) {
        if (table == null) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        return waiter.takeOrder(table, menuChoice, isHappyHourActive);
    }

    public boolean takeOrder(Table table, int menuChoice) {
        return takeOrder(table, menuChoice, false);
    }

    public boolean serveFood(Table table, GameScoreManager scoreManager) {
        if (table == null) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        boolean served = waiter.serve(table);
        if (served && scoreManager != null) {
            scoreManager.recordServed();
        }
        return served;
    }

    public void waiterWait() {
        waiter.waitTurn();
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public boolean hasWaiterActed() {
        return waiter.hasActed();
    }

    // ==========================================
    // CHEF ACTIONS
    // ==========================================

    public boolean cookDish(Table table) {
        if (table == null) {
            System.out.println("[DENIED] Invalid table choice for Chef.");
            return false;
        }
        return chef.cook(table);
    }

    public void chefWait() {
        chef.waitTurn();
    }

    public Chef getChef() {
        return chef;
    }

    public boolean hasChefActed() {
        return chef.hasActed();
    }

    // ==========================================
    // CASHIER ACTIONS
    // ==========================================

    /**
     * Cashier automatically handles at most 1 payment per turn, oldest first (FIFO from PaymentQueue).
     * Strictly enforces assignment rule: A customer who became READY_TO_PAY this turn pays next turn at earliest.
     * Records revenue and satisfaction in the GameScoreManager.
     *
     * @param paymentQueue Queue of tables with customers ready to pay
     * @param scoreManager Score manager to record payments and revenue
     * @param currentTurn  Current game turn number
     * @return Bill amount collected in cents
     */
    public int processCashierPayment(PaymentQueue paymentQueue, GameScoreManager scoreManager, int currentTurn) {
        if (paymentQueue != null && !paymentQueue.isEmpty()) {
            Table readyTable = paymentQueue.peek();
            if (readyTable.isOccupied()) {
                Customer c = readyTable.getseatedCustomer();
                // A customer who became READY_TO_PAY this turn pays next turn at earliest (Section 3.2 Step 4)
                if (c.getTurnBecameReadyToPay() >= currentTurn) {
                    return 0; // Still in their READY_TO_PAY turn; pays next turn at earliest
                }
                int billCents = cashier.processPaymentCents(readyTable);
                if (billCents > 0 || cashier.hasActed()) {
                    paymentQueue.dequeue();
                    if (scoreManager != null) {
                        scoreManager.recordPayment(billCents, c.getSatisfaction());
                    }
                    return billCents;
                }
            } else {
                paymentQueue.dequeue(); // Clean up stale entry if table was cleared
            }
        }
        return 0;
    }

    public int processCashierPayment(PaymentQueue paymentQueue, GameScoreManager scoreManager) {
        return processCashierPayment(paymentQueue, scoreManager, Integer.MAX_VALUE);
    }

    public Cashier getCashier() {
        return cashier;
    }

    public boolean hasCashierActed() {
        return cashier.hasActed();
    }
}
