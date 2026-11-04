package entity.queue;

import entity.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the FIFO payment queue for customers waiting to pay.
 * Guarantees that the Cashier processes payments in 'oldest first' order.
 */
public class PaymentQueue {
    private final ArrayList<Table> readyTables = new ArrayList<>();

    public void enqueue(Table table) {
        if (table != null && !readyTables.contains(table)) {
            readyTables.add(table);
        }
    }

    public Table dequeue() {
        if (readyTables.isEmpty()) {
            return null;
        }
        return readyTables.remove(0);
    }

    public Table peek() {
        if (readyTables.isEmpty()) {
            return null;
        }
        return readyTables.get(0);
    }

    public boolean isEmpty() {
        return readyTables.isEmpty();
    }

    public int size() {
        return readyTables.size();
    }

    public List<Table> getReadyTables() {
        return Collections.unmodifiableList(readyTables);
    }

    public void remove(Table table) {
        readyTables.remove(table);
    }
}

