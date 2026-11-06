package control;

import entity.Table;
import entity.customer.CriticCustomer;
import entity.customer.Customer;
import entity.customer.CustomerState;
import entity.customer.RegularCustomer;
import entity.customer.VIPCustomer;
import entity.queue.PaymentQueue;
import entity.queue.WaitingQueue;

/**
 * Handles customer lifecycle management: scheduled arrivals,
 * Happy Hour satisfaction recovery, eating progression, satisfaction decay,
 * and abandonment (Single Responsibility Principle).
 */
public class CustomerController {
    private final String[] predefinedNames = { "Alice", "Bob", "Charlie", "David", "Edward", "Faith" };
    private int nameIndex = 0;

    /**
     * Processes customer arrivals scheduled for turns 1, 4, 7, 10, 13, 16.
     * Generates a 6-customer sequence: Regular, VIP, Critic, Regular, VIP, Critic.
     */
    public Customer processArrivals(int turn, WaitingQueue queue) {
        if (turn == 1 || turn == 4 || turn == 7 || turn == 10 || turn == 13 || turn == 16) {
            String customerName;
            if (nameIndex < predefinedNames.length) {
                customerName = predefinedNames[nameIndex++];
            } else {
                customerName = "Cust_" + (nameIndex + 1);
                nameIndex++;
            }

            int customerTypeIndex = (turn - 1) / 3;
            Customer newCustomer;
            switch (customerTypeIndex % 3) {
                case 1:
                    newCustomer = new VIPCustomer(customerName);
                    break;
                case 2:
                    newCustomer = new CriticCustomer(customerName);
                    break;
                default:
                    newCustomer = new RegularCustomer(customerName);
                    break;
            }
            queue.add(newCustomer);
            System.out.println("[ARRIVAL] " + newCustomer.getName() + " ("
                    + newCustomer.getClass().getSimpleName()
                    + ") entered the queue (Satisfaction: 100).");
            return newCustomer;
        }
        return null;
    }

    /**
     * Executes Happy Hour turn-start satisfaction recovery (Section 4.3).
     * Customers still waiting for a table (in queue) or waiting for food (seated, unserved)
     * recover 15 satisfaction (maximum 100).
     *
     * @param waitingQueue Queue of waiting customers.
     * @param tables       Seated customer tables.
     */
    public void processHappyHourRecovery(WaitingQueue waitingQueue, Table[] tables) {
        // 1. Customers waiting in queue for a table
        for (int i = 0; i < waitingQueue.size(); i++) {
            Customer c = waitingQueue.get(i);
            int oldSat = c.getSatisfaction();
            if (oldSat < 100) {
                c.setSatisfaction(Math.min(100, oldSat + 15));
                System.out.println("[HAPPY HOUR] " + c.getName() + " (" + c.getClass().getSimpleName()
                        + ") recovered satisfaction in queue (" + oldSat + " -> " + c.getSatisfaction() + ").");
            }
        }

        // 2. Seated customers waiting for food (SEATED or ORDERED only)
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isOccupied()) {
                Customer c = tables[i].getseatedCustomer();
                if (c.getState() == CustomerState.SEATED || c.getState() == CustomerState.ORDERED) {
                    int oldSat = c.getSatisfaction();
                    if (oldSat < 100) {
                        c.setSatisfaction(Math.min(100, oldSat + 15));
                        System.out.println("[HAPPY HOUR] " + c.getName() + " (" + c.getClass().getSimpleName()
                                + ") recovered satisfaction at Table " + tables[i].getTableNumber()
                                + " (" + oldSat + " -> " + c.getSatisfaction() + ").");
                    }
                }
            }
        }
    }

    /**
     * Automatically transitions customers who finished eating 1 full turn after being SERVED
     * into READY_TO_PAY and enqueues their table into the PaymentQueue.
     */
    public void processEatingProgression(Table[] tables, PaymentQueue paymentQueue, int turn) {
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isOccupied()) {
                Customer c = tables[i].getseatedCustomer();
                if (c.getState() == CustomerState.SERVED) {
                    c.setState(CustomerState.READY_TO_PAY);
                    c.setTurnBecameReadyToPay(turn);
                    paymentQueue.enqueue(tables[i]);
                    System.out.println("[STATUS] " + c.getName()
                            + " finished eating and entered the payment queue (READY_TO_PAY).");
                }
            }
        }
    }

    /**
     * Executes turn-end satisfaction decay polymorphically across waiting customers and seated customers.
     * Removes unhappy customers who reach 0 satisfaction (abandonment).
     */
    public void processDecayAndAbandonment(WaitingQueue waitingQueue, Table[] tables, PaymentQueue paymentQueue, GameScoreManager scoreManager) {
        // 1. Decay for Queue customers
        for (int i = waitingQueue.size() - 1; i >= 0; i--) {
            Customer c = waitingQueue.get(i);
            c.decaySatisfaction();
            if (c.abandonment()) {
                System.out.println("[ABANDON] " + c.getName() + " (" + c.getClass().getSimpleName()
                        + ") left the queue unhappy! (Satisfaction reached 0)");
                waitingQueue.remove(i);
                if (scoreManager != null) {
                    scoreManager.recordDeparture();
                }
            }
        }

        // 2. Decay for Seated customers
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isOccupied()) {
                Customer c = tables[i].getseatedCustomer();
                c.decaySatisfaction();
                if (c.abandonment()) {
                    System.out.println("[ABANDON] " + c.getName() + " (" + c.getClass().getSimpleName()
                            + ") left Table " + (i + 1) + " unhappy without paying!");
                    paymentQueue.remove(tables[i]);
                    tables[i].clearTable();
                    if (scoreManager != null) {
                        scoreManager.recordDeparture();
                    }
                }
            }
        }
    }
}
