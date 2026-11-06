package entity.staff;

import entity.Menu;
import entity.MenuItem;
import entity.Order;
import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

/**
 * Waiter staff role.
 * Seats customers, takes customer orders, and serves prepared food.
 * Extends Staff to eliminate duplicate state and uphold LSP.
 */
public class Waiter extends Staff {

    @Override
    public String getRoleName() {
        return "Waiter";
    }

    public boolean seat(Customer customer, Table table) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Waiter has already taken an action this turn.");
            return false;
        }
        if (customer == null) {
            System.out.println("[DENIED] No customer to seat.");
            return false;
        }
        if (table == null) {
            System.out.println("[DENIED] Invalid table.");
            return false;
        }
        if (table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is already occupied by: "
                    + table.getseatedCustomer().getName());
            return false;
        }

        table.seat(customer);
        customer.setState(CustomerState.SEATED);
        this.actedThisTurn = true;
        System.out.println("[ACTION] Waiter seated " + customer.getName() + " ("
                + customer.getClass().getSimpleName() + ") at Table " + table.getTableNumber());
        return true;
    }

    /**
     * Takes order at a table where the seated customer selects food according to their own preference.
     * Locks the price at order creation time, incorporating Happy Hour discounts if active (Section 4.3).
     *
     * @param table             Table to take order for.
     * @param isHappyHourActive Whether Happy Hour is currently active.
     * @return true if order was taken successfully, false otherwise.
     */
    public boolean takeOrder(Table table, boolean isHappyHourActive) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Waiter has already taken an action this turn.");
            return false;
        }
        if (table == null) {
            System.out.println("[DENIED] Invalid table.");
            return false;
        }
        if (!table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is empty! Seat a customer first.");
            return false;
        }
        Customer customer = table.getseatedCustomer();
        if (customer.getOrder() != null) {
            System.out.println("[DENIED] Customer at Table " + table.getTableNumber() + " already ordered.");
            return false;
        }

        int choice = customer.chooseMenuChoice();
        return takeOrder(table, choice, isHappyHourActive);
    }

    public boolean takeOrder(Table table, int menuChoice, boolean isHappyHourActive) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Waiter has already taken an action this turn.");
            return false;
        }
        if (table == null) {
            System.out.println("[DENIED] Invalid table.");
            return false;
        }
        if (!table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is empty! Seat a customer first.");
            return false;
        }
        Customer customer = table.getseatedCustomer();
        if (customer.getOrder() != null) {
            System.out.println("[DENIED] Customer at Table " + table.getTableNumber() + " already ordered.");
            return false;
        }

        MenuItem item = Menu.Choice(menuChoice);
        int lockedPriceCents = customer.computeOrderBillCents(item, isHappyHourActive);
        Order order = new Order(item, lockedPriceCents);
        customer.setOrder(order);
        customer.setState(CustomerState.ORDERED);
        this.actedThisTurn = true;

        System.out.println("[ACTION] Took order: " + customer.getName() + " ("
                + customer.getClass().getSimpleName() + ") chose " + order.getFoodName()
                + " ($" + String.format("%.2f", order.getPrice()) + ", " + order.getPrepUnitsLeft()
                + " prep unit(s)) at Table " + table.getTableNumber());
        return true;
    }

    public boolean takeOrder(Table table) {
        return takeOrder(table, false);
    }

    public boolean takeOrder(Table table, int menuChoice) {
        return takeOrder(table, menuChoice, false);
    }

    public boolean serve(Table table) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Waiter has already taken an action this turn.");
            return false;
        }
        if (table == null) {
            System.out.println("[DENIED] Invalid table.");
            return false;
        }
        if (!table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is empty!");
            return false;
        }
        Customer customer = table.getseatedCustomer();
        Order order = customer.getOrder();
        if (order == null) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " has not ordered yet!");
            return false;
        }
        if (!order.isReady()) {
            System.out.println("[DENIED] Food is not READY yet (" + order.getPrepUnitsLeft() + " prep unit(s) left)!");
            return false;
        }
        if (order.isServed()) {
            System.out.println("[DENIED] Food is already served at Table " + table.getTableNumber());
            return false;
        }

        order.serve();
        customer.setState(CustomerState.SERVED);
        this.actedThisTurn = true;
        System.out.println("[ACTION] Waiter served " + order.getFoodName() + " to " + customer.getName()
                + " (" + customer.getClass().getSimpleName() + ") at Table " + table.getTableNumber());
        return true;
    }
}
