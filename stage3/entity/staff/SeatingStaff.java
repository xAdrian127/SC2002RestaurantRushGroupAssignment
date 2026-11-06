package entity.staff;

import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

/**
 * Intermediate abstract specialization for staff roles capable of seating queued customers
 * at available tables (Host and Waiter). Eliminates duplicate seating logic (DRY / LSP).
 */
public abstract class SeatingStaff extends Staff {

    /**
     * Seats a customer at the specified table if the staff member has not acted yet
     * and the table is unoccupied.
     *
     * @param customer Customer to seat.
     * @param table    Table to seat at.
     * @return true if seating succeeded, false otherwise.
     */
    public boolean seat(Customer customer, Table table) {
        if (hasActed() || customer == null || table == null || table.isOccupied()) {
            return false;
        }
        table.seat(customer);
        customer.setState(CustomerState.SEATED);
        markActed();
        return true;
    }
}
