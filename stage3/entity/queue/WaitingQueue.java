package entity.queue;

import entity.customer.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates waiting queue operations for arriving customers.
 */
public class WaitingQueue {
    private final ArrayList<Customer> customers = new ArrayList<>();

    public void add(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
        }
    }

    public Customer removeFirst() {
        if (customers.isEmpty()) {
            return null;
        }
        return customers.remove(0);
    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public int size() {
        return customers.size();
    }

    public Customer get(int index) {
        return customers.get(index);
    }

    public Customer remove(int index) {
        return customers.remove(index);
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }
}

