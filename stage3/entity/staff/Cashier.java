package entity.staff;

import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

/**
 * Cashier staff member.
 * Automatically processes at most 1 payment per turn,
 * handling the oldest READY_TO_PAY customer.
 * Extends Staff to eliminate duplicate state and uphold LSP.
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
     * @param table Table occupied by a customer who is READY_TO_PAY
     * @return The bill amount collected in integer cents, or 0 if payment could not be processed
     */
    public int processPaymentCents(Table table) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Cashier has already processed a payment this turn.");
            return 0;
        }
        if (table == null || !table.isOccupied()) {
            return 0;
        }
        Customer customer = table.getseatedCustomer();
        if (customer.getState() != CustomerState.READY_TO_PAY) {
            return 0;
        }

        int cents = customer.calculateBillCents();
        customer.setState(CustomerState.PAID);
        if (customer.getOrder() != null) {
            customer.getOrder().markPaid();
        }
        table.clearTable();
        this.actedThisTurn = true;

        System.out.println("[CASHIER] " + customer.getName() + " (" + customer.getClass().getSimpleName() + ") paid $"
                + String.format("%.2f", cents / 100.0)
                + " (Recorded Satisfaction: " + customer.getSatisfaction() + ") and left Table " + table.getTableNumber()
                + ". Table " + table.getTableNumber() + " is now free!");

        return cents;
    }

    public double processPayment(Table table) {
        return processPaymentCents(table) / 100.0;
    }
}
