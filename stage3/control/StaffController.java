package control;

import entity.Order;
import entity.Table;
import entity.customer.Customer;
import entity.queue.PaymentQueue;
import entity.queue.WaitingQueue;
import entity.staff.Cashier;
import entity.staff.Chef;
import entity.staff.Host;
import entity.staff.SeatingStaff;
import entity.staff.Staff;
import entity.staff.Waiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages staff coordination and action dispatching (Single Responsibility Principle).
 * Encapsulates Host seating, Waiter tasks, Chef cooking, Cashier billing, and turn action rules
 * without performing any console I/O (BCE).
 */
public class StaffController {
    private final Host host = new Host();
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();
    private final List<Staff> staffMembers = Arrays.asList(host, waiter, chef, cashier);

    /**
     * Resets action flags polymorphically for all staff members at the start of a turn.
     */
    public void resetStaffTurns() {
        for (Staff staff : staffMembers) {
            staff.resetTurn();
        }
    }

    // ==========================================
    // SHARED SEATING COORDINATION (Host & Waiter)
    // ==========================================

    private ActionResult seatWithStaff(SeatingStaff staff, WaitingQueue waitingQueue, Table table) {
        if (staff.hasActed()) {
            return ActionResult.denied("[DENIED] " + staff.getRoleName() + " has already taken an action this turn.");
        }
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice.");
        }
        if (waitingQueue == null || waitingQueue.isEmpty()) {
            return ActionResult.denied("[DENIED] Cannot seat customer: waiting queue is empty.");
        }
        if (table.isOccupied()) {
            return ActionResult.denied("[DENIED] Table " + table.getTableNumber() + " is already occupied by: "
                    + table.getSeatedCustomer().getName());
        }

        Customer customer = waitingQueue.removeFirst();
        if (!staff.seat(customer, table)) {
            waitingQueue.add(customer);
            return ActionResult.denied("[DENIED] " + staff.getRoleName() + " could not seat customer.");
        }

        return ActionResult.success("[ACTION] " + staff.getRoleName() + " seated " + customer.getName()
                + " (" + customer.getCustomerType() + ") at Table " + table.getTableNumber());
    }

    // ==========================================
    // HOST ACTIONS (Stage 3)
    // ==========================================

    public ActionResult hostSeatCustomer(WaitingQueue waitingQueue, Table table) {
        return seatWithStaff(host, waitingQueue, table);
    }

    public ActionResult hostWait() {
        host.waitTurn();
        return ActionResult.success("[ACTION] " + host.getRoleName() + " remained idle / waited this turn.");
    }

    public boolean hasHostActed() {
        return host.hasActed();
    }

    // ==========================================
    // WAITER ACTIONS
    // ==========================================

    public ActionResult seatCustomer(WaitingQueue waitingQueue, Table table) {
        return seatWithStaff(waiter, waitingQueue, table);
    }

    public ActionResult takeOrder(Table table, boolean isHappyHourActive) {
        if (waiter.hasActed()) {
            return ActionResult.denied("[DENIED] Waiter has already taken an action this turn.");
        }
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice.");
        }
        if (!table.isOccupied()) {
            return ActionResult.denied("[DENIED] Table " + table.getTableNumber() + " is empty! Seat a customer first.");
        }
        Customer customer = table.getSeatedCustomer();
        if (customer.hasOrdered()) {
            return ActionResult.denied("[DENIED] Customer at Table " + table.getTableNumber() + " already ordered.");
        }

        if (!waiter.takeOrder(table, isHappyHourActive)) {
            return ActionResult.denied("[DENIED] Could not take order at Table " + table.getTableNumber() + ".");
        }

        Order order = customer.getOrder();
        return ActionResult.success("[ACTION] Took order: " + customer.getName() + " ("
                + customer.getCustomerType() + ") chose " + order.getFoodName()
                + " ($" + String.format("%.2f", order.getPrice()) + ", " + order.getPrepUnitsLeft()
                + " prep unit(s)) at Table " + table.getTableNumber());
    }

    public ActionResult serveFood(Table table, GameScoreManager scoreManager) {
        if (waiter.hasActed()) {
            return ActionResult.denied("[DENIED] Waiter has already taken an action this turn.");
        }
        if (table == null) {
            return ActionResult.denied("[DENIED] Invalid table choice.");
        }
        if (!table.isOccupied()) {
            return ActionResult.denied("[DENIED] Table " + table.getTableNumber() + " is empty!");
        }
        Customer customer = table.getSeatedCustomer();
        Order order = customer.getOrder();
        if (order == null) {
            return ActionResult.denied("[DENIED] Table " + table.getTableNumber() + " has not ordered yet!");
        }
        if (!order.isReady()) {
            return ActionResult.denied("[DENIED] Food is not READY yet (" + order.getPrepUnitsLeft() + " prep unit(s) left)!");
        }
        if (order.isServed()) {
            return ActionResult.denied("[DENIED] Food is already served at Table " + table.getTableNumber());
        }

        if (!waiter.serve(table)) {
            return ActionResult.denied("[DENIED] Could not serve food at Table " + table.getTableNumber() + ".");
        }

        if (scoreManager != null) {
            scoreManager.recordServed();
        }
        return ActionResult.success("[ACTION] Waiter served " + order.getFoodName() + " to " + customer.getName()
                + " (" + customer.getCustomerType() + ") at Table " + table.getTableNumber());
    }

    public ActionResult waiterWait() {
        waiter.waitTurn();
        return ActionResult.success("[ACTION] " + waiter.getRoleName() + " remained idle / waited this turn.");
    }

    public boolean hasWaiterActed() {
        return waiter.hasActed();
    }

    // ==========================================
    // CHEF ACTIONS
    // ==========================================

    public ActionResult cookDish(Table table) {
        if (chef.hasActed()) {
            return ActionResult.denied("[DENIED] Chef has already taken an action this turn.");
        }
        if (table == null || !table.isOccupied()) {
            return ActionResult.denied("[DENIED] Table is not occupied.");
        }
        Customer customer = table.getSeatedCustomer();
        Order order = customer.getOrder();
        if (order == null) {
            return ActionResult.denied("[DENIED] No active order at Table " + table.getTableNumber());
        }
        if (order.isReady()) {
            return ActionResult.denied("[DENIED] Dish at Table " + table.getTableNumber() + " is already READY!");
        }

        if (!chef.cook(table)) {
            return ActionResult.denied("[DENIED] Could not cook order at Table " + table.getTableNumber() + ".");
        }

        List<String> messages = new ArrayList<>();
        messages.add("[CHEF] Cooked 1 unit for " + customer.getName() + "'s " + order.getFoodName()
                + " (" + order.getPrepUnitsLeft() + " prep unit(s) left)");
        if (order.isReady()) {
            messages.add("[CHEF] " + order.getFoodName() + " for " + customer.getName()
                    + " is now READY! (Can be served next turn)");
        }
        return ActionResult.success(messages);
    }

    public ActionResult chefWait() {
        chef.waitTurn();
        return ActionResult.success("[ACTION] " + chef.getRoleName() + " waited this turn.");
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
     * Records revenue and satisfaction in GameScoreManager and appends payment event messages.
     */
    public int processCashierPayment(
            PaymentQueue paymentQueue,
            GameScoreManager scoreManager,
            int currentTurn,
            List<String> events) {
        if (paymentQueue == null || paymentQueue.isEmpty()) {
            return 0;
        }

        Table readyTable = paymentQueue.peek();
        if (!readyTable.isOccupied()) {
            paymentQueue.dequeue();
            return 0;
        }

        Customer customer = readyTable.getSeatedCustomer();
        if (customer.getTurnBecameReadyToPay() >= currentTurn) {
            return 0;
        }

        String customerName = customer.getName();
        String customerType = customer.getCustomerType();
        int recordedSatisfaction = customer.getSatisfaction();
        int tableNumber = readyTable.getTableNumber();

        int billCents = cashier.processPaymentCents(readyTable);
        if (billCents > 0 || cashier.hasActed()) {
            paymentQueue.dequeue();
            if (scoreManager != null) {
                scoreManager.recordPayment(billCents, recordedSatisfaction);
            }
            if (events != null) {
                events.add("[CASHIER] " + customerName + " (" + customerType + ") paid $"
                        + String.format("%.2f", billCents / 100.0)
                        + " (Recorded Satisfaction: " + recordedSatisfaction + ") and left Table " + tableNumber
                        + ". Table " + tableNumber + " is now free!");
            }
            return billCents;
        }
        return 0;
    }

    public boolean hasCashierActed() {
        return cashier.hasActed();
    }
}
