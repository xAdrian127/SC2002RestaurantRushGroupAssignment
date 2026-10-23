
// Main.java (Stage 1 v1 - Procedural Game Driver following Official Specification)
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // --- Stage 1 Base Setting (Section 2.1) ---
        List<Customer> waitingQueue = new ArrayList<>();
        Table[] tables = new Table[] {
                new Table(1),
                new Table(2) }; // 2 dining tables

        double totalRevenue = 0.0;
        int paidCustomerCount = 0;
        int servedCustomerCount = 0;
        int unhappyDepartures = 0;
        int totalPaidSatisfaction = 0;
        int customerIdCounter = 1;

        // Staff state flags (v1 procedural approach)
        boolean isWaiterBusy = false;
        boolean isChefWorking = false;

        System.out.println("==========================================");
        System.out.println("  RESTAURANT RUSH - STAGE 1 (VERSION 1)  ");
        System.out.println("==========================================");

        // --- 18-Turn Main Game Loop (Always runs all 18 turns) ---
        for (int turn = 1; turn <= 18; turn++) {
            // Calculate current average satisfaction of paid customers
            String avgSatStart = (paidCustomerCount == 0) ? "N/A"
                    : String.format("%.1f", (double) totalPaidSatisfaction / paidCustomerCount);

            System.out.println("\n==========================================");
            System.out.println(">>> TURN " + turn + " of 18 <<<");
            System.out.println("Revenue: $" + String.format("%.2f", totalRevenue)
                    + " | Paid: " + paidCustomerCount
                    + " | Avg Sat: " + avgSatStart);
            System.out.println("==========================================");

            // Reset staff action flags at the start of each turn
            isWaiterBusy = false;
            isChefWorking = false;

            // ----------------------------------------------------
            // STEP 1: Scheduled Customer Arrivals (Turns 1, 4, 7, 10, 13, 16)
            // ----------------------------------------------------
            if (turn == 1 || turn == 4 || turn == 7 || turn == 10 || turn == 13 || turn == 16) {
                Customer newCustomer = new Customer("Cust_" + customerIdCounter++);
                waitingQueue.add(newCustomer);
                System.out.println("[ARRIVAL] " + newCustomer.getName() + " entered the queue (Satisfaction: 100).");
            }

            // Render current queue & table status
            System.out.println("Waiting Queue (" + waitingQueue.size() + "): " + getQueueNames(waitingQueue));
            printTableStatus(tables);

            // ----------------------------------------------------
            // STEP 2A: Manager Choice - WAITER
            // ----------------------------------------------------
            boolean waiterActionDone = false;
            while (!waiterActionDone) {
                System.out.println("\n[WAITER ACTION] Select Waiter Task for Turn " + turn + ":");
                System.out.println("1. Seat customer at Table 1");
                System.out.println("2. Seat customer at Table 2");
                System.out.println("3. Take Order at a Table");
                System.out.println("4. Serve READY Food to a Table");
                System.out.println("5. Do Nothing / Wait");
                System.out.print("Waiter Choice > ");

                int choice = getSafeIntInput(scanner);
                if (choice == -1) {
                    return; // Input stream closed
                }

                if (choice == 1 || choice == 2) {
                    int tableIdx = choice - 1;
                    if (tables[tableIdx].isOccupied()) {
                        System.out.println("[INVALID] Table " + (tableIdx + 1)
                                + " is already occupied! Please choose another action.");
                    } else if (waitingQueue.isEmpty()) {
                        System.out.println(
                                "[INVALID] Queue is empty, no customer to seat! Please choose another action.");
                    } else {
                        Customer seatedCust = waitingQueue.remove(0);
                        tables[tableIdx].setOccupiedBy(seatedCust);
                        isWaiterBusy = true;
                        System.out.println(
                                "[ACTION] Waiter seated " + seatedCust.getName() + " at Table " + (tableIdx + 1));
                        waiterActionDone = true;
                    }
                } else if (choice == 3) {
                    System.out.print("Which Table to take order for? (1 or 2, 0 to cancel): ");
                    int tInput = getSafeIntInput(scanner);
                    if (tInput == 0) {
                        System.out.println("[CANCEL] Waiter action cancelled.");
                        continue;
                    }
                    int tNum = tInput - 1;
                    if (tNum < 0 || tNum >= tables.length) {
                        System.out.println("[INVALID] Invalid table number! Enter 1 or 2.");
                    } else if (!tables[tNum].isOccupied()) {
                        System.out
                                .println("[INVALID] Table " + (tNum + 1) + " is empty! (Seat a customer there first)");
                    } else if (tables[tNum].getOccupiedBy().getOrder() != null) {
                        System.out.println("[INVALID] Table " + (tNum + 1) + " has already ordered!");
                    } else {
                        Customer c = tables[tNum].getOccupiedBy();
                        // Customer chooses their own food from the menu
                        int customerFoodChoice = random.nextInt(3) + 1;
                        Order newOrder = createOrderFromChoice(customerFoodChoice);
                        c.setOrder(newOrder);
                        isWaiterBusy = true;
                        System.out.println("[ACTION] Took order: " + c.getName() + " chose " + newOrder.getFoodName()
                                + " ($" + String.format("%.2f", newOrder.getPrice()) + ", "
                                + newOrder.getPrepUnitsLeft()
                                + " prep unit(s)) at Table " + (tNum + 1));
                        waiterActionDone = true;
                    }
                } else if (choice == 4) {
                    System.out.print("Which Table to serve READY food to? (1 or 2, 0 to cancel): ");
                    int tInput = getSafeIntInput(scanner);
                    if (tInput == 0) {
                        System.out.println("[CANCEL] Waiter action cancelled.");
                        continue;
                    }
                    int tNum = tInput - 1;
                    if (tNum < 0 || tNum >= tables.length) {
                        System.out.println("[INVALID] Invalid table number! Enter 1 or 2.");
                    } else if (!tables[tNum].isOccupied()) {
                        System.out.println("[INVALID] Table " + (tNum + 1) + " is empty!");
                    } else {
                        Customer c = tables[tNum].getOccupiedBy();
                        Order ord = c.getOrder();
                        if (ord == null) {
                            System.out.println("[INVALID] Table " + (tNum + 1) + " has not placed an order yet!");
                        } else if (!ord.isReady()) {
                            System.out.println("[INVALID] Food is not READY yet (" + ord.getPrepUnitsLeft()
                                    + " prep unit(s) left)!");
                        } else if (ord.isServed()) {
                            System.out.println("[INVALID] Food has already been served to Table " + (tNum + 1) + "!");
                        } else {
                            ord.setServed(true);
                            ord.setTurnServed(turn);
                            servedCustomerCount++;
                            isWaiterBusy = true;
                            System.out.println("[ACTION] Waiter served " + ord.getFoodName() + " to " + c.getName()
                                    + " at Table " + (tNum + 1));
                            waiterActionDone = true;
                        }
                    }
                } else if (choice == 5) {
                    System.out.println("[ACTION] Waiter remained idle / waited this turn.");
                    waiterActionDone = true;
                } else {
                    System.out.println("[INVALID] Choice must be between 1 and 5. Please try again.");
                }
            }

            // ----------------------------------------------------
            // STEP 2B: Manager Choice - CHEF
            // (Chef may work on an order the Waiter took this turn)
            // ----------------------------------------------------
            List<Integer> cookableTables = new ArrayList<>();
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].isOccupied() && tables[i].getOccupiedBy().getOrder() != null) {
                    Order ord = tables[i].getOccupiedBy().getOrder();
                    if (!ord.isReady()) {
                        cookableTables.add(i);
                    }
                }
            }

            if (cookableTables.isEmpty()) {
                System.out.println("\n[CHEF ACTION] No orders currently need cooking. Chef is waiting.");
            } else {
                boolean chefActionDone = false;
                while (!chefActionDone) {
                    System.out.println("\n[CHEF ACTION] Select Chef Task for Turn " + turn + ":");
                    for (int k = 0; k < cookableTables.size(); k++) {
                        int tIdx = cookableTables.get(k);
                        Order ord = tables[tIdx].getOccupiedBy().getOrder();
                        System.out.println((k + 1) + ". Cook for Table " + (tIdx + 1) + " (" + ord.getFoodName()
                                + ", " + ord.getPrepUnitsLeft() + " prep unit(s) left)");
                    }
                    int waitOption = cookableTables.size() + 1;
                    System.out.println(waitOption + ". Do Nothing / Wait");
                    System.out.print("Chef Choice > ");

                    int chefChoice = getSafeIntInput(scanner);
                    if (chefChoice == -1) {
                        return; // End of stream
                    }

                    if (chefChoice >= 1 && chefChoice <= cookableTables.size()) {
                        int chosenTableIdx = cookableTables.get(chefChoice - 1);
                        Customer c = tables[chosenTableIdx].getOccupiedBy();
                        Order ord = c.getOrder();
                        ord.setPrepUnitsLeft(ord.getPrepUnitsLeft() - 1);
                        isChefWorking = true;
                        System.out.println("[CHEF] Cooked 1 unit for " + c.getName() + "'s " + ord.getFoodName()
                                + " (" + ord.getPrepUnitsLeft() + " prep unit(s) left)");
                        if (ord.isReady()) {
                            System.out.println("[CHEF] " + ord.getFoodName() + " for " + c.getName()
                                    + " is now READY! (Can be served next turn)");
                        }
                        chefActionDone = true;
                    } else if (chefChoice == waitOption) {
                        System.out.println("[CHEF] Chef remained idle / waited this turn.");
                        chefActionDone = true;
                    } else {
                        System.out.println(
                                "[INVALID] Choice must be between 1 and " + waitOption + ". Please try again.");
                    }
                }
            }

            // ----------------------------------------------------
            // STEP 4: Eating and Payment (Cashier automatic)
            // ----------------------------------------------------
            // 4a. Eating progression: A customer served on an earlier turn finishes 1 full
            // turn of eating
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].isOccupied()) {
                    Customer c = tables[i].getOccupiedBy();
                    Order ord = c.getOrder();
                    if (ord != null && ord.isServed() && !ord.isReadyToPay()) {
                        if (ord.getTurnServed() < turn) {
                            ord.setReadyToPay(true);
                            ord.setTurnReadyToPay(turn);
                            System.out.println("[EATING] " + c.getName() + " finished eating and is now READY_TO_PAY.");
                        }
                    }
                }
            }

            // 4b. Cashier automatically collects payment (at most 1 per turn, oldest first)
            // A customer who became READY_TO_PAY this turn pays next turn at earliest
            int tableToPay = -1;
            int earliestReadyTurn = Integer.MAX_VALUE;
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].isOccupied()) {
                    Customer c = tables[i].getOccupiedBy();
                    Order ord = c.getOrder();
                    if (ord != null && ord.isReadyToPay() && ord.getTurnReadyToPay() < turn) {
                        if (ord.getTurnReadyToPay() < earliestReadyTurn) {
                            earliestReadyTurn = ord.getTurnReadyToPay();
                            tableToPay = i;
                        }
                    }
                }
            }

            if (tableToPay != -1) {
                Customer c = tables[tableToPay].getOccupiedBy();
                Order ord = c.getOrder();
                totalRevenue += ord.getPrice();
                paidCustomerCount++;
                totalPaidSatisfaction += c.getSatisfaction();
                System.out.println("[CASHIER] " + c.getName() + " paid $" + String.format("%.2f", ord.getPrice())
                        + " (Recorded Satisfaction: " + c.getSatisfaction() + ") and left Table " + (tableToPay + 1));
                tables[tableToPay].clearTable(); // Free table
            }

            // ----------------------------------------------------
            // STEP 5: Waiting and Abandonment (-8 per turn for unserved)
            // (Customers who are served, eating or ready to pay lose nothing)
            // ----------------------------------------------------
            // 5a. Queue satisfaction decay
            for (int i = waitingQueue.size() - 1; i >= 0; i--) {
                Customer c = waitingQueue.get(i);
                c.setSatisfaction(Math.max(0, c.getSatisfaction() - 8));
                if (c.getSatisfaction() <= 0) {
                    System.out
                            .println("[ABANDON] " + c.getName() + " left the queue unhappy! (Satisfaction reached 0)");
                    waitingQueue.remove(i);
                    unhappyDepartures++;
                }
            }

            // 5b. Seated table satisfaction decay (only unserved customers lose
            // satisfaction)
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].isOccupied()) {
                    Customer c = tables[i].getOccupiedBy();
                    Order ord = c.getOrder();
                    boolean hasBeenServed = (ord != null && ord.isServed());
                    if (!hasBeenServed) {
                        c.setSatisfaction(Math.max(0, c.getSatisfaction() - 8));
                        if (c.getSatisfaction() <= 0) {
                            System.out.println(
                                    "[ABANDON] " + c.getName() + " left Table " + (i + 1) + " unhappy without paying!");
                            tables[i].clearTable(); // Cancel unfinished order & free table
                            unhappyDepartures++;
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // END OF TURN SUMMARY
            // ----------------------------------------------------
            String avgSatEnd = (paidCustomerCount == 0) ? "N/A"
                    : String.format("%.1f", (double) totalPaidSatisfaction / paidCustomerCount);

            System.out.println("\n------------------------------------------");
            System.out.println("=== END OF TURN " + turn + " SUMMARY ===");
            System.out.println("Turns Used                 : " + turn + " / 18");
            System.out.println("Served Count               : " + servedCustomerCount);
            System.out.println("Paid Customers             : " + paidCustomerCount);
            System.out.println("Unhappy Departures         : " + unhappyDepartures);
            System.out.println("Revenue                    : $" + String.format("%.2f", totalRevenue));
            System.out.println("Average Paid Satisfaction  : " + avgSatEnd);
            System.out.println("------------------------------------------");
        } // End of 18 turns

        // ----------------------------------------------------
        // FINAL GAME SUMMARY & VICTORY EVALUATION (Section 2.1)
        // ----------------------------------------------------
        String finalAvgSat = (paidCustomerCount == 0) ? "N/A"
                : String.format("%.1f", (double) totalPaidSatisfaction / paidCustomerCount);

        double avgSatNumeric = (paidCustomerCount == 0) ? 0.0 : ((double) totalPaidSatisfaction / paidCustomerCount);
        boolean targetCustomersMet = paidCustomerCount >= 4;
        boolean targetRevenueMet = totalRevenue >= 45.0;
        boolean targetSatisfactionMet = avgSatNumeric >= 60.0;
        boolean isVictory = targetCustomersMet && targetRevenueMet && targetSatisfactionMet;

        System.out.println("\n==========================================");
        System.out.println("       GAME OVER - FINAL RESULTS          ");
        System.out.println("==========================================");
        System.out.println("Turns Used                 : 18 / 18");
        System.out.println("Served Count               : " + servedCustomerCount);
        System.out.println("Paid Customers             : " + paidCustomerCount + " (Target: >= 4) -> "
                + (targetCustomersMet ? "PASSED" : "FAILED"));
        System.out.println("Unhappy Departures         : " + unhappyDepartures);
        System.out.println(
                "Total Revenue              : $" + String.format("%.2f", totalRevenue) + " (Target: >= $45.00) -> "
                        + (targetRevenueMet ? "PASSED" : "FAILED"));
        System.out.println("Average Paid Satisfaction  : " + finalAvgSat + " (Target: >= 60.0) -> "
                + (targetSatisfactionMet ? "PASSED" : "FAILED"));
        System.out.println("------------------------------------------");
        System.out.println("OVERALL RESULT             : " + (isVictory ? "VICTORY!" : "DEFEAT!"));
        System.out.println("==========================================");

        scanner.close();
    }

    // Helper method to safely read integers without crashing on non-numeric input
    private static int getSafeIntInput(Scanner scanner) {
        while (true) {
            if (!scanner.hasNext()) {
                return -1; // End of stream
            }
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
            String badToken = scanner.next();
            System.out.print("[INVALID] '" + badToken + "' is not a number. Please enter a valid number: ");
        }
    }

    private static Order createOrderFromChoice(int choice) {
        switch (choice) {
            case 1:
                return new Order("Salad", 9.0, 1);
            case 2:
                return new Order("Burger", 15.0, 1);
            case 3:
                return new Order("Pasta", 18.0, 2);
            default:
                return null;
        }
    }

    // Helper method to print queue
    private static String getQueueNames(List<Customer> queue) {
        if (queue.isEmpty())
            return "Empty";
        StringBuilder sb = new StringBuilder();
        for (Customer c : queue) {
            sb.append(c.getName()).append("(Sat:").append(c.getSatisfaction()).append(") ");
        }
        return sb.toString();
    }

    // Helper method to print tables
    private static void printTableStatus(Table[] tables) {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isOccupied()) {
                System.out.println("Table " + (i + 1) + ": [EMPTY]");
            } else {
                Customer c = tables[i].getOccupiedBy();
                Order o = c.getOrder();
                String orderInfo;
                if (o == null) {
                    orderInfo = "No Order";
                } else if (o.isReadyToPay()) {
                    orderInfo = o.getFoodName() + " [READY TO PAY]";
                } else if (o.isServed()) {
                    orderInfo = o.getFoodName() + " [EATING]";
                } else if (o.isReady()) {
                    orderInfo = o.getFoodName() + " [READY TO SERVE]";
                } else {
                    orderInfo = o.getFoodName() + " (PrepLeft: " + o.getPrepUnitsLeft() + ")";
                }
                System.out.println("Table " + (i + 1) + ": [" + c.getName() + " | Sat: " +
                        c.getSatisfaction() + " | Order: " + orderInfo + "]");
            }
        }
    }
}
