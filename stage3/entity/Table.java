package entity;

import entity.customer.Customer;

/**
 * Represents a dining table in the restaurant.
 * Encapsulates seating state and provides Law-of-Demeter helpers for active orders.
 */
public class Table {
    private final int tableNumber;
    private Customer seatedCustomer;

    public Table(int tableNumber) {
        if (tableNumber <= 0) {
            throw new IllegalArgumentException("Table number must be positive");
        }
        this.tableNumber = tableNumber;
        this.seatedCustomer = null;
    }

    public void seat(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Cannot seat a null customer");
        }
        if (isOccupied()) {
            throw new IllegalStateException("Table " + tableNumber + " is already occupied");
        }
        this.seatedCustomer = customer;
    }

    public boolean isOccupied() {
        return seatedCustomer != null;
    }

    public void clearTable() {
        this.seatedCustomer = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Customer getSeatedCustomer() {
        return seatedCustomer;
    }

    public Order getActiveOrder() {
        return seatedCustomer != null ? seatedCustomer.getOrder() : null;
    }

    public boolean hasCookableOrder() {
        return seatedCustomer != null && seatedCustomer.hasCookableOrder();
    }
}
