package entity.staff;

import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

/**
 * Host staff role (Stage 3).
 * Seats at most one queued customer per turn in addition to the Waiter's task.
 * Extends Staff to reuse common turn-tracking state (Generalization / LSP).
 */
public class Host extends Staff {

    @Override
    public String getRoleName() {
        return "Host";
    }

    /**
     * Seats a customer at the specified table.
     * Enforces the quota of at most 1 seating action per turn.
     *
     * @param customer Customer to seat.
     * @param table    Table to seat at.
     * @return true if seating succeeded, false otherwise.
     */
    public boolean seat(Customer customer, Table table) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Host has already taken an action this turn.");
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
        System.out.println("[ACTION] Host seated " + customer.getName() + " ("
                + customer.getClass().getSimpleName() + ") at Table " + table.getTableNumber());
        return true;
    }
}
