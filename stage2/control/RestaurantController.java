import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantController {
    // Customer names
    private final String[] predefinedNames = { "Alice", "Bob", "Charlie", "David", "Edward", "Faith" };
    private int nameIndex = 0;

    private final List<Customer> waitingQueue = new ArrayList<>();
    private final Table[] tables = new Table[2]; // fixed 2 tables for Stage 1

    // Staff instances
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();

    // Game metrics
    private double totalRevenue = 0.0;
    private int paidCustomerCount = 0;
    private int servedCustomerCount = 0;
    private int unhappyDepartures = 0;
    private double totalSatisfactionOfPaidCustomer = 0.0;
    private int currentTurn = 1;

    // Staff Turn Constraint Flags
    private int tableBecameReadyThisTurn = -1;

    public RestaurantController() {
        for (int i = 0; i < tables.length; i++) {
            tables[i] = new Table(i + 1);
        }
    }

    // Turn Start & Customer Arrivals (Turns 1, 4, 7, 10, 13, 16)
    public void proccessTurnStart(int turn_num) {
        this.currentTurn = turn_num;
        waiter.resetTurn();
        chef.resetTurn();
        tableBecameReadyThisTurn = -1;

        System.out.println("\n==========================================");
        System.out.println(">>> TURN " + turn_num + " of 18 <<<");
        System.out.println("Revenue: $" + String.format("%.2f", totalRevenue)
                + " | Paid: " + paidCustomerCount
                + " | Avg Sat: " + getAverageSatisfactionString());
        System.out.println("==========================================");

        // 1. Scheduled arrivals
        if (turn_num == 1 || turn_num == 4 || turn_num == 7 || turn_num == 10 || turn_num == 13 || turn_num == 16) {
            String customerName;
            if (nameIndex < predefinedNames.length) {
                customerName = predefinedNames[nameIndex++];
            } else {
                customerName = "Cust_" + (nameIndex + 1);
                nameIndex++;
            }
            // Cycle through Regular, VIP, and Critic customers.
            // Arrival turns are 1, 4, 7, 10, 13, and 16.
            int customerTypeIndex = (turn_num - 1) / 3;
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
            waitingQueue.add(newCustomer);
            System.out.println("[ARRIVAL] " + newCustomer.getName() + " ("
                    + newCustomer.getClass().getSimpleName()
                    + ") entered the queue (Satisfaction: 100).");
        }

        // 2. Automatic State Progression: 1 turn after SERVED, transition to READY_TO_PAY
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isOccupied()) {
                Customer c = tables[i].getseatedCustomer();
                if (c.getState() == CustomerState.SERVED) {
                    c.setState(CustomerState.READY_TO_PAY);
                    tableBecameReadyThisTurn = i;
                    System.out.println("[STATUS] " + c.getName() + " finished eating and is now READY_TO_PAY.");
                }
            }
        }
    }

    // Manager / Waiter Actions
    public boolean seatCustomer(int tableIndex) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice (must be 1 to " + tables.length + ").");
            return false;
        }
        if (waitingQueue.isEmpty()) {
            System.out.println("[DENIED] Cannot seat customer: waiting queue is empty.");
            return false;
        }
        Customer c = waitingQueue.get(0);
        boolean seated = waiter.seat(c, tables[tableIndex]);
        if (seated) {
            waitingQueue.remove(0);
            return true;
        }
        return false;
    }

    public boolean takeOrder(int tableIndex, int menu_choice) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        return waiter.takeOrder(tables[tableIndex], menu_choice);
    }

    public boolean serveFood(int tableIndex) {
        if (tableIndex < 0 || tableIndex >= tables.length) {
            System.out.println("[DENIED] Invalid table choice.");
            return false;
        }
        boolean served = waiter.serve(tables[tableIndex]);
        if (served) {
            servedCustomerCount++;
            return true;
        }
        return false;
    }

    public void waiterWait() {
        waiter.waitTurn();
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public boolean hasWaiterActed() {
        return waiter.hasActed();
    }

    // Chef Actions
    public boolean cookDish(int table_index) {
        if (table_index < 0 || table_index >= tables.length) {
            System.out.println("[DENIED] Invalid table choice for Chef.");
            return false;
        }
        return chef.cook(tables[table_index]);
    }

    public void chefWait() {
        chef.waitTurn();
    }

    public Chef getChef() {
        return chef;
    }

    public boolean hasChefActed() {
        return chef.hasActed();
    }

    // Turn Completion, Payments & Satisfaction Decay
    public void processTurnEnd() {
        // 1. Process Cashier Payments (Automated for READY_TO_PAY customers, at most 1 per turn)
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isOccupied()) {
                Customer c = tables[i].getseatedCustomer();
                if (c.getState() == CustomerState.READY_TO_PAY && i != tableBecameReadyThisTurn) {
                    double bill = c.getOrder().getPrice();
                    if (c instanceof VIPCustomer) {
                        bill *= 0.90; // VIP customers receive a 10% discount.
                    }
                    totalRevenue += bill;
                    paidCustomerCount++;
                    totalSatisfactionOfPaidCustomer += c.getSatisfaction();
                    c.setState(CustomerState.PAID);
                    System.out.println("[CASHIER] " + c.getName() + " paid $" + String.format("%.2f", bill)
                            + " (Recorded Satisfaction: " + c.getSatisfaction() + ") and left Table " + (i + 1));
                    tables[i].clearTable(); // Free table cleanly
                    break; // At most one payment handled per turn
                }
            }
        }

        // 2. Satisfaction Decay for Queue (-8 per turn)
        for (int i = waitingQueue.size() - 1; i >= 0; i--) {
            Customer c = waitingQueue.get(i);
            c.decreaseSatisfication(8);
            if (c.abandonment()) {
                System.out.println("[ABANDON] " + c.getName() + " left the queue unhappy! (Satisfaction reached 0)");
                waitingQueue.remove(i);
                unhappyDepartures++;
            }
        }

        // 3. Satisfaction Decay for Seated Customers (-8 per turn, only if unserved)
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].isOccupied()) {
                Customer c = tables[i].getseatedCustomer();
                c.decreaseSatisfication(8);
                if (c.abandonment()) {
                    System.out.println("[ABANDON] " + c.getName() + " left Table " + (i + 1) + " unhappy without paying!");
                    tables[i].clearTable();
                    unhappyDepartures++;
                }
            }
        }
    }

    // Getters for view layer
    public int getCurrentTurn() {
        return currentTurn;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getPaidCustomerCount() {
        return paidCustomerCount;
    }

    public int getServedCustomerCount() {
        return servedCustomerCount;
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }

    public double getTotalSatisfactionOfPaidCustomer() {
        return totalSatisfactionOfPaidCustomer;
    }

    public String getAverageSatisfactionString() {
        if (paidCustomerCount == 0) {
            return "N/A";
        }
        return String.format("%.1f", totalSatisfactionOfPaidCustomer / paidCustomerCount);
    }

    public double getAverageSatisfactionNumeric() {
        if (paidCustomerCount == 0) {
            return 0.0;
        }
        return totalSatisfactionOfPaidCustomer / paidCustomerCount;
    }

    public List<Customer> getWaitingQueue() {
        return Collections.unmodifiableList(waitingQueue);
    }

    public Table[] getTables() {
        return tables;
    }

    public boolean isGameOver() {
        return currentTurn > 18;
    }
}
