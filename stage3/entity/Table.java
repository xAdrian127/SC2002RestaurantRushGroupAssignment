package entity;

import entity.customer.Customer;

public class Table {
    private int tableNumber;
    private Customer seatedCustomer;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.seatedCustomer = null;
    }

    public void seat(Customer customer) {
        this.seatedCustomer = customer;
    }

    public boolean isOccupied() {
        return seatedCustomer != null;
    }

    public void clearTable() {
        this.seatedCustomer = null;
    }

    // getters and setters
    public int getTableNumber() {
        return tableNumber;
    }

    public Customer getseatedCustomer() {
        return seatedCustomer;
    }

    public void setseatedCustomer(Customer customer) {
        this.seatedCustomer = customer;
    }

    // Aliases for convenience across controllers/view
    public Customer getOccupiedBy() {
        return seatedCustomer;
    }

    public void setOccupiedBy(Customer customer) {
        this.seatedCustomer = customer;
    }
}

