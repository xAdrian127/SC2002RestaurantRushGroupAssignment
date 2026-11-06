package entity.staff;

import entity.Menu;
import entity.MenuItem;
import entity.Order;
import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

/**
 * Waiter staff role.
 * Seats customers (via SeatingStaff), takes customer orders, and serves prepared food.
 * Pure domain entity with zero console I/O (BCE / SRP).
 */
public class Waiter extends SeatingStaff {

    @Override
    public String getRoleName() {
        return "Waiter";
    }

    /**
     * Takes an order at an occupied table where the seated customer selects food
     * according to their own deterministic preference.
     * Locks the price at order creation time, incorporating Happy Hour discounts if active (Section 4.3).
     *
     * @param table             Table to take order for.
     * @param isHappyHourActive Whether Happy Hour is currently active.
     * @return true if order was taken successfully, false otherwise.
     */
    public boolean takeOrder(Table table, boolean isHappyHourActive) {
        if (hasActed() || table == null || !table.isOccupied()) {
            return false;
        }
        Customer customer = table.getSeatedCustomer();
        if (customer.hasOrdered()) {
            return false;
        }
        int choice = customer.chooseMenuChoice();
        return takeOrder(table, choice, isHappyHourActive);
    }

    public boolean takeOrder(Table table, int menuChoice, boolean isHappyHourActive) {
        if (hasActed() || table == null || !table.isOccupied()) {
            return false;
        }
        Customer customer = table.getSeatedCustomer();
        if (customer.hasOrdered()) {
            return false;
        }

        MenuItem item = Menu.getItem(menuChoice);
        int lockedPriceCents = customer.computeOrderBillCents(item, isHappyHourActive);
        Order order = new Order(item, lockedPriceCents);
        customer.setOrder(order);
        customer.setState(CustomerState.ORDERED);
        markActed();
        return true;
    }

    public boolean serve(Table table) {
        if (hasActed() || table == null || !table.isOccupied()) {
            return false;
        }
        Customer customer = table.getSeatedCustomer();
        Order order = customer.getOrder();
        if (order == null || !order.isReady() || order.isServed()) {
            return false;
        }

        order.serve();
        customer.setState(CustomerState.SERVED);
        markActed();
        return true;
    }
}
