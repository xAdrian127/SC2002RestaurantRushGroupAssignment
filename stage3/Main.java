import control.RestaurantController;
import entity.Order;
import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        RestaurantController controller = new RestaurantController();

        System.out.println("==========================================");
        System.out.println("             RESTAURANT RUSH              ");
        System.out.println("==========================================");

        // --- 18-Turn Main Game Loop (Always runs all 18 turns) ---
        for (int turn = 1; turn <= 18; turn++) {
            // Step 1: Process Arrivals & Start of Turn Header
            controller.processTurnStart(turn);

            // Display current queue & table status
            System.out.println("Waiting Queue (" + controller.getWaitingQueue().size() + "): "
                    + getQueueNames(controller.getWaitingQueue()));
            printTableStatus(controller.getTables());

            // ----------------------------------------------------
            // STEP 2A: Manager Choice - WAITER
            // ----------------------------------------------------
            boolean waiterActionDone = false;
            while (!waiterActionDone) {
                System.out.println("\n[WAITER ACTION] Select Waiter Task for Turn " + turn + ":");
                System.out.println("1. Seat a customer");
                System.out.println("2. Take Order at a Table");
                System.out.println("3. Serve READY Food to a Table");
                System.out.println("4. Do Nothing / Wait");
                System.out.print("Waiter Choice > ");

                int choice = getSafeIntInput(scanner);
                if (choice == -1) {
                    return; // End of input stream
                }

                if (choice == 1) {
                    if (controller.getWaitingQueue().isEmpty()) {
                        System.out.println("[INVALID] Queue is empty, no customer to seat! Choose another action.");
                        continue;
                    }
                    System.out.print("Which Table to seat at? (1 or 2, 0 to cancel): ");
                    int tInput = getSafeIntInput(scanner);
                    if (tInput == 0) {
                        System.out.println("[CANCEL] Waiter action cancelled.");
                        continue;
                    }
                    int tableIdx = tInput - 1;
                    if (controller.seatCustomer(tableIdx)) {
                        waiterActionDone = true;
                    }
                } else if (choice == 2) {
                    System.out.print("Which Table to take order for? (1 or 2, 0 to cancel): ");
                    int tInput = getSafeIntInput(scanner);
                    if (tInput == 0) {
                        System.out.println("[CANCEL] Waiter action cancelled.");
                        continue;
                    }
                    int tableIdx = tInput - 1;
                    // Customer chooses their own food according to domain object rules
                    if (controller.takeOrder(tableIdx)) {
                        waiterActionDone = true;
                    }
                } else if (choice == 3) {
                    System.out.print("Which Table to serve READY food to? (1 or 2, 0 to cancel): ");
                    int tInput = getSafeIntInput(scanner);
                    if (tInput == 0) {
                        System.out.println("[CANCEL] Waiter action cancelled.");
                        continue;
                    }
                    int tableIdx = tInput - 1;
                    if (controller.serveFood(tableIdx)) {
                        waiterActionDone = true;
                    }
                } else if (choice == 4) {
                    controller.waiterWait();
                    System.out.println("[ACTION] Waiter remained idle / waited this turn.");
                    waiterActionDone = true;
                } else {
                    System.out.println("[INVALID] Choice must be between 1 and 4. Please try again.");
                }
            }

            // ----------------------------------------------------
            // STEP 2B: Manager Choice - CHEF
            // (Chef may work on an order the Waiter took this turn)
            // ----------------------------------------------------
            Table[] currentTables = controller.getTables();
            List<Integer> cookableTables = new ArrayList<>();
            for (int i = 0; i < currentTables.length; i++) {
                if (currentTables[i].isOccupied()) {
                    Order o = currentTables[i].getseatedCustomer().getOrder();
                    if (o != null && !o.isReady()) {
                        cookableTables.add(i);
                    }
                }
            }

            boolean chefActionDone = false;
            while (!chefActionDone) {
                System.out.println("\n[CHEF ACTION] Select Chef Task for Turn " + turn + ":");
                if (cookableTables.isEmpty()) {
                    System.out.println("No pending orders need cooking. Chef must wait.");
                    System.out.println("1. Do Nothing / Wait");
                    System.out.print("Chef Choice > ");
                    int cChoice = getSafeIntInput(scanner);
                    if (cChoice == -1) {
                        return;
                    }
                    if (cChoice == 1) {
                        controller.chefWait();
                        System.out.println("[ACTION] Chef waited this turn.");
                        chefActionDone = true;
                    } else {
                        System.out.println("[INVALID] Only option 1 is available.");
                    }
                } else if (cookableTables.size() == 1) {
                    int targetTableIdx = cookableTables.get(0);
                    Table targetTable = currentTables[targetTableIdx];
                    Order o = targetTable.getseatedCustomer().getOrder();
                    System.out.println("1. Cook order for Table " + targetTable.getTableNumber()
                            + " (" + o.getFoodName() + ", " + o.getPrepUnitsLeft() + " prep unit(s) left)");
                    System.out.println("2. Do Nothing / Wait");
                    System.out.print("Chef Choice > ");
                    int cChoice = getSafeIntInput(scanner);
                    if (cChoice == -1) {
                        return;
                    }
                    if (cChoice == 1) {
                        if (controller.cookDish(targetTableIdx)) {
                            chefActionDone = true;
                        }
                    } else if (cChoice == 2) {
                        controller.chefWait();
                        System.out.println("[ACTION] Chef waited this turn.");
                        chefActionDone = true;
                    } else {
                        System.out.println("[INVALID] Choice must be 1 or 2.");
                    }
                } else {
                    // Two pending orders
                    int tIdx1 = cookableTables.get(0);
                    int tIdx2 = cookableTables.get(1);
                    Order o1 = currentTables[tIdx1].getseatedCustomer().getOrder();
                    Order o2 = currentTables[tIdx2].getseatedCustomer().getOrder();

                    System.out.println("1. Cook order for Table " + currentTables[tIdx1].getTableNumber()
                            + " (" + o1.getFoodName() + ", " + o1.getPrepUnitsLeft() + " prep unit(s) left)");
                    System.out.println("2. Cook order for Table " + currentTables[tIdx2].getTableNumber()
                            + " (" + o2.getFoodName() + ", " + o2.getPrepUnitsLeft() + " prep unit(s) left)");
                    System.out.println("3. Do Nothing / Wait");
                    System.out.print("Chef Choice > ");
                    int cChoice = getSafeIntInput(scanner);
                    if (cChoice == -1) {
                        return;
                    }
                    if (cChoice == 1) {
                        if (controller.cookDish(tIdx1)) {
                            chefActionDone = true;
                        }
                    } else if (cChoice == 2) {
                        if (controller.cookDish(tIdx2)) {
                            chefActionDone = true;
                        }
                    } else if (cChoice == 3) {
                        controller.chefWait();
                        System.out.println("[ACTION] Chef waited this turn.");
                        chefActionDone = true;
                    } else {
                        System.out.println("[INVALID] Choice must be 1, 2, or 3.");
                    }
                }
            }

            // ----------------------------------------------------
            // Step 3-5: Turn End (Decay & Abandonment)
            // ----------------------------------------------------
            controller.processTurnEnd();

            // End of Turn summary
            System.out.println("\n--- End of Turn " + turn + " Summary ---");
            System.out.println("Served Count               : " + controller.getServedCustomerCount());
            System.out.println("Unhappy Departures         : " + controller.getUnhappyDepartures());
            System.out.println("Revenue                    : $" + String.format("%.2f", controller.getTotalRevenue()));
            System.out.println("Paid Customers             : " + controller.getPaidCustomerCount());
            System.out.println("Average Paid Satisfaction  : " + controller.getAverageSatisfactionString());
            System.out.println("Turns Used                 : " + turn + " / 18");
        }

        // --- Step 6: End Game Evaluation ---
        printEndGame(controller);
        scanner.close();
    }

    // Safe input helper handling non-numeric tokens
    private static int getSafeIntInput(Scanner scanner) {
        while (true) {
            if (!scanner.hasNext()) {
                return -1;
            }
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
            String badToken = scanner.next();
            System.out.print("[INVALID] '" + badToken + "' is not a number. Please enter a valid number: ");
        }
    }

    // Helper method to format queue display
    private static String getQueueNames(List<Customer> queue) {
        if (queue.isEmpty()) {
            return "Empty";
        }
        StringBuilder sb = new StringBuilder();
        for (Customer c : queue) {
            sb.append(c.getName()).append("(").append(c.getClass().getSimpleName())
                    .append(", Sat:").append(c.getSatisfaction()).append(") ");
        }
        return sb.toString();
    }

    // Helper method to format table status
    private static void printTableStatus(Table[] tables) {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i].isOccupied()) {
                System.out.println("Table " + (i + 1) + ": [EMPTY]");
            } else {
                Customer c = tables[i].getseatedCustomer();
                Order o = c.getOrder();
                String orderInfo;
                if (o == null) {
                    orderInfo = "No Order";
                } else if (c.getState() == CustomerState.READY_TO_PAY) {
                    orderInfo = o.getFoodName() + " [READY TO PAY]";
                } else if (c.getState() == CustomerState.SERVED || o.isServed()) {
                    orderInfo = o.getFoodName() + " [EATING]";
                } else if (o.isReady()) {
                    orderInfo = o.getFoodName() + " [READY TO SERVE]";
                } else {
                    orderInfo = o.getFoodName() + " (PrepLeft: " + o.getPrepUnitsLeft() + ")";
                }
                System.out.println("Table " + (i + 1) + ": [" + c.getName() + " (" + c.getClass().getSimpleName()
                        + ") | Sat: " + c.getSatisfaction() + " | Order: " + orderInfo + "]");
            }
        }
    }

    // helper method to print final result
    private static void printEndGame(RestaurantController controller) {
        boolean targetCustomersMet = controller.getPaidCustomerCount() >= 4;
        boolean targetRevenueMet = controller.getTotalRevenue() >= 45.0;
        boolean targetSatisfactionMet = controller.getAverageSatisfactionNumeric() >= 60.0;
        boolean isVictory = targetCustomersMet && targetRevenueMet && targetSatisfactionMet;

        System.out.println("\n==========================================");
        System.out.println("       GAME OVER - FINAL RESULTS          ");
        System.out.println("==========================================");
        System.out.println("Turns Used                 : 18 / 18");
        System.out.println("Served Count               : " + controller.getServedCustomerCount());
        System.out.println("Paid Customers             : " + controller.getPaidCustomerCount()
                + " (Target: >= 4) -> " + (targetCustomersMet ? "PASSED" : "FAILED"));
        System.out.println("Unhappy Departures         : " + controller.getUnhappyDepartures());
        System.out.println("Total Revenue              : $" + String.format("%.2f", controller.getTotalRevenue())
                + " (Target: >= $45.00) -> " + (targetRevenueMet ? "PASSED" : "FAILED"));
        System.out.println("Average Paid Satisfaction  : " + controller.getAverageSatisfactionString()
                + " (Target: >= 60.0) -> " + (targetSatisfactionMet ? "PASSED" : "FAILED"));
        System.out.println("------------------------------------------");
        System.out.println("OVERALL RESULT             : " + (isVictory ? "VICTORY!" : "DEFEAT!"));
        System.out.println("==========================================");
    }
}
