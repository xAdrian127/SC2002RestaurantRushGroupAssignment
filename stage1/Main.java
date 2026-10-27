
// Main.java (Stage 1 - View & CLI Driver using RestaurantController)
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        RestaurantController controller = new RestaurantController();

        System.out.println("==========================================");
        System.out.println("             RESTAURANT RUSH              ");
        System.out.println("==========================================");

        // --- 18-Turn Main Game Loop (Always runs all 18 turns) ---
        for (int turn = 1; turn <= 18; turn++) {
            // Step 1: Process Arrivals & Start of Turn Header
            controller.proccessTurnStart(turn);

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
                    // Customer chooses their own food randomly from the menu
                    int customerFoodChoice = random.nextInt(Menu.getItemCount()) + 1;
                    if (controller.takeOrder(tableIdx, customerFoodChoice)) {
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
                    Customer c = currentTables[i].getseatedCustomer();
                    if (c.getOrder() != null && !c.getOrder().isReady()) {
                        cookableTables.add(i);
                    }
                }
            }

            if (cookableTables.isEmpty()) {
                controller.chefWait();
                System.out.println("\n[CHEF ACTION] No orders currently need cooking. Chef is waiting.");
            } else {
                boolean chefActionDone = false;
                while (!chefActionDone) {
                    System.out.println("\n[CHEF ACTION] Select Chef Task for Turn " + turn + ":");
                    for (int k = 0; k < cookableTables.size(); k++) {
                        int tIdx = cookableTables.get(k);
                        Order ord = currentTables[tIdx].getseatedCustomer().getOrder();
                        System.out.println((k + 1) + ". Cook for Table " + (tIdx + 1) + " (" + ord.getFoodName()
                                + ", " + ord.getPrepUnitsLeft() + " prep unit(s) left)");
                    }
                    int waitOption = cookableTables.size() + 1;
                    System.out.println(waitOption + ". Do Nothing / Wait");
                    System.out.print("Chef Choice > ");

                    int chefChoice = getSafeIntInput(scanner);
                    if (chefChoice == -1) {
                        return;
                    }

                    if (chefChoice >= 1 && chefChoice <= cookableTables.size()) {
                        int chosenTableIdx = cookableTables.get(chefChoice - 1);
                        if (controller.cookDish(chosenTableIdx)) {
                            chefActionDone = true;
                        }
                    } else if (chefChoice == waitOption) {
                        controller.chefWait();
                        System.out.println("[CHEF] Chef remained idle / waited this turn.");
                        chefActionDone = true;
                    } else {
                        System.out.println(
                                "[INVALID] Choice must be between 1 and " + waitOption + ". Please try again.");
                    }
                }
            }

            // ----------------------------------------------------
            // STEP 4 & 5: Eating, Cashier Payments & Satisfaction Decay
            // ----------------------------------------------------
            controller.processTurnEnd();

            // ----------------------------------------------------
            // END OF TURN SUMMARY
            // ----------------------------------------------------
            System.out.println("\n------------------------------------------");
            System.out.println("=== END OF TURN " + turn + " SUMMARY ===");
            System.out.println("Turns Used                 : " + turn + " / 18");
            System.out.println("Served Count               : " + controller.getServedCustomerCount());
            System.out.println("Paid Customers             : " + controller.getPaidCustomerCount());
            System.out.println("Unhappy Departures         : " + controller.getUnhappyDepartures());
            System.out.println("Revenue                    : $" + String.format("%.2f", controller.getTotalRevenue()));
            System.out.println("Average Paid Satisfaction  : " + controller.getAverageSatisfactionString());
            System.out.println("------------------------------------------");
            printTableStatus(controller.getTables());
        } // End of 18 turns

        // ----------------------------------------------------
        // FINAL GAME SUMMARY & VICTORY EVALUATION (Section 2.1)
        // ----------------------------------------------------

        printEndGame(controller);
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

    // Helper method to format queue display
    private static String getQueueNames(List<Customer> queue) {
        if (queue.isEmpty()) {
            return "Empty";
        }
        StringBuilder sb = new StringBuilder();
        for (Customer c : queue) {
            sb.append(c.getName()).append("(Sat:").append(c.getSatisfaction()).append(") ");
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
                System.out.println("Table " + (i + 1) + ": [" + c.getName() + " | Sat: " +
                        c.getSatisfaction() + " | Order: " + orderInfo + "]");
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
