package control;

import entity.Table;
import entity.customer.CriticCustomer;
import entity.customer.Customer;
import entity.customer.CustomerState;
import entity.customer.RegularCustomer;
import entity.customer.VIPCustomer;
import entity.queue.PaymentQueue;
import entity.queue.WaitingQueue;

import java.util.List;

/**
 * Handles customer lifecycle management: scheduled arrivals,
 * Happy Hour satisfaction recovery, eating progression, satisfaction decay,
 * and abandonment (Single Responsibility Principle).
 * Emits structured event messages instead of performing console I/O (BCE).
 */
public class CustomerController {
    private static final int HAPPY_HOUR_RECOVERY_POINTS = 15;
    private static final String[] PREDEFINED_NAMES = {
            "Alice", "Bob", "Charlie", "David", "Edward", "Faith"
    };

    private int arrivalCount = 0;

    /**
     * Processes customer arrivals scheduled for turns 1, 4, 7, 10, 13, 16.
     * Generates a 6-customer sequence: Regular, VIP, Critic, Regular, VIP, Critic.
     */
    public Customer processArrivals(int turn, WaitingQueue queue, List<String> events) {
        if (turn == 1 || turn == 4 || turn == 7 || turn == 10 || turn == 13 || turn == 16) {
            int arrivalNumber = ++arrivalCount;
            String customerName = (arrivalNumber <= PREDEFINED_NAMES.length)
                    ? PREDEFINED_NAMES[arrivalNumber - 1]
                    : "Cust_" + arrivalNumber;

            int customerTypeIndex = (turn - 1) / 3;
            Customer newCustomer;
            switch (customerTypeIndex % 3) {
                case 1:
                    newCustomer = new VIPCustomer(customerName, arrivalNumber);
                    break;
                case 2:
                    newCustomer = new CriticCustomer(customerName, arrivalNumber);
                    break;
                default:
                    newCustomer = new RegularCustomer(customerName, arrivalNumber);
                    break;
            }
            queue.add(newCustomer);
            if (events != null) {
                events.add("[ARRIVAL] " + newCustomer.getName() + " ("
                        + newCustomer.getCustomerType()
                        + ") entered the queue (Satisfaction: " + newCustomer.getSatisfaction() + ").");
            }
            return newCustomer;
        }
        return null;
    }

    /**
     * Executes Happy Hour turn-start satisfaction recovery (Section 4.3).
     * Delegates eligibility and cap rules to Customer.recoverSatisfaction (Information Expert).
     */
    public void processHappyHourRecovery(WaitingQueue waitingQueue, List<Table> tables, List<String> events) {
        // 1. Customers waiting in queue for a table
        for (int i = 0; i < waitingQueue.size(); i++) {
            Customer customer = waitingQueue.get(i);
            int recovered = customer.recoverSatisfaction(HAPPY_HOUR_RECOVERY_POINTS);
            if (recovered > 0 && events != null) {
                int oldSat = customer.getSatisfaction() - recovered;
                events.add("[HAPPY HOUR] " + customer.getName() + " (" + customer.getCustomerType()
                        + ") recovered satisfaction in queue (" + oldSat + " -> " + customer.getSatisfaction() + ").");
            }
        }

        // 2. Seated customers waiting for food (SEATED or ORDERED only)
        for (Table table : tables) {
            if (table.isOccupied()) {
                Customer customer = table.getSeatedCustomer();
                int recovered = customer.recoverSatisfaction(HAPPY_HOUR_RECOVERY_POINTS);
                if (recovered > 0 && events != null) {
                    int oldSat = customer.getSatisfaction() - recovered;
                    events.add("[HAPPY HOUR] " + customer.getName() + " (" + customer.getCustomerType()
                            + ") recovered satisfaction at Table " + table.getTableNumber()
                            + " (" + oldSat + " -> " + customer.getSatisfaction() + ").");
                }
            }
        }
    }

    /**
     * Automatically transitions customers who finished eating 1 full turn after being SERVED
     * into READY_TO_PAY and enqueues their table into the PaymentQueue.
     */
    public void processEatingProgression(List<Table> tables, PaymentQueue paymentQueue, int turn, List<String> events) {
        for (Table table : tables) {
            if (table.isOccupied()) {
                Customer customer = table.getSeatedCustomer();
                if (customer.getState() == CustomerState.SERVED) {
                    customer.setState(CustomerState.READY_TO_PAY);
                    customer.setTurnBecameReadyToPay(turn);
                    paymentQueue.enqueue(table);
                    if (events != null) {
                        events.add("[STATUS] " + customer.getName()
                                + " finished eating and entered the payment queue (READY_TO_PAY).");
                    }
                }
            }
        }
    }

    /**
     * Executes turn-end satisfaction decay polymorphically across waiting and seated customers.
     * Removes unhappy customers who reach 0 satisfaction (abandonment).
     */
    public void processDecayAndAbandonment(
            WaitingQueue waitingQueue,
            List<Table> tables,
            PaymentQueue paymentQueue,
            GameScoreManager scoreManager,
            List<String> events) {
        // 1. Decay for queued customers
        for (int i = waitingQueue.size() - 1; i >= 0; i--) {
            Customer customer = waitingQueue.get(i);
            customer.decaySatisfaction();
            if (customer.hasAbandoned()) {
                if (events != null) {
                    events.add("[ABANDON] " + customer.getName() + " (" + customer.getCustomerType()
                            + ") left the queue unhappy! (Satisfaction reached 0)");
                }
                waitingQueue.remove(i);
                if (scoreManager != null) {
                    scoreManager.recordDeparture();
                }
            }
        }

        // 2. Decay for seated customers
        for (Table table : tables) {
            if (table.isOccupied()) {
                Customer customer = table.getSeatedCustomer();
                customer.decaySatisfaction();
                if (customer.hasAbandoned()) {
                    if (events != null) {
                        events.add("[ABANDON] " + customer.getName() + " (" + customer.getCustomerType()
                                + ") left Table " + table.getTableNumber() + " unhappy without paying!");
                    }
                    paymentQueue.remove(table);
                    table.clearTable();
                    if (scoreManager != null) {
                        scoreManager.recordDeparture();
                    }
                }
            }
        }
    }
}
