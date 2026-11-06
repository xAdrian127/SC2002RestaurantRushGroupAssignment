package entity.staff;

import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

/**
 * Cashier staff member.
 * Automatically processes at most 1 payment per turn,
 * handling the oldest READY_TO_PAY customer.
 * Pure domain entity with zero console I/O (BCE / SRP).
 */
public class Cashier extends Staff {

    @Override
    public String getRoleName() {
        return "Cashier";
    }

    /**
     * Processes payment for a seated customer at a table in integer cents.
     * Calculates bill polymorphically, updates customer and order state to PAID,
     * clears the table, and records the cashier's action.
     *
     * @param table Table occupied by a customer who is READY_TO_PAY.
     * @return The bill amount collected in integer cents, or 0 if payment could not be processed.
     */
    public int processPaymentCents(Table table) {
        if (hasActed() || table == null || !table.isOccupied()) {
            return 0;
        }
        Customer customer = table.getSeatedCustomer();
        if (customer.getState() != CustomerState.READY_TO_PAY) {
            return 0;
        }

        int cents = customer.calculateBillCents();
        customer.setState(CustomerState.PAID);
        if (customer.getOrder() != null) {
            customer.getOrder().markPaid();
        }
        table.clearTable();
        markActed();
        return cents;
    }
}
