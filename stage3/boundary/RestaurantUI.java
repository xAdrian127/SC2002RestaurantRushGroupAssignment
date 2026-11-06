package boundary;

import control.RestaurantController;
import entity.Order;
import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Boundary class handling all CLI presentation and user input collection (BCE Architecture).
 * Decouples presentation formatting and console I/O from domain logic and Main.java.
 */
public class RestaurantUI {
    private final Scanner scanner;

    public RestaurantUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public RestaurantUI() {
        this(new Scanner(System.in));
    }

    // ==========================================
    // PRESENTATION & RENDERING
    // ==========================================

    public void displayWelcomeBanner() {
        System.out.println("==========================================");
        System.out.println("             RESTAURANT RUSH              ");
        System.out.println("==========================================");
    }

    public void displayTurnStatus(RestaurantController controller) {
        System.out.println("Waiting Queue (" + controller.getWaitingQueue().size() + "): "
                + formatQueueNames(controller.getWaitingQueue()));
        printTableStatus(controller.getTables());
    }

    public void displayTurnSummary(RestaurantController controller, int turn) {
        System.out.println("\n--- End of Turn " + turn + " Summary ---");
        System.out.println("Served Count               : " + controller.getServedCustomerCount());
        System.out.println("Unhappy Departures         : " + controller.getUnhappyDepartures());
        System.out.println("Revenue                    : $" + String.format("%.2f", controller.getTotalRevenue()));
        System.out.println("Paid Customers             : " + controller.getPaidCustomerCount());
        System.out.println("Average Paid Satisfaction  : " + controller.getAverageSatisfactionString());
        System.out.println("Turns Used                 : " + turn + " / 18");
    }

    public void displayEndGame(RestaurantController controller) {
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

    // ==========================================
    // USER INPUT COLLECTION (Step by Step)
    // ==========================================

    /**
     * Prompts the player to optionally activate Happy Hour at turn start (once per game).
     *
     * @return true to continue, false if input stream terminated.
     */
    public boolean handleHappyHourPrompt(RestaurantController controller, int turn) {
        if (!controller.isHappyHourActivated()) {
            System.out.println("\n[HAPPY HOUR POLICY] Happy Hour is available (Activate once per game; lasts 2 turns with 20% order discount & +15 sat boost).");
            while (true) {
                System.out.print("Activate Happy Hour for Turn " + turn + "? (1 = Yes, 2 = No): ");
                int hhChoice = getSafeIntInput();
                if (hhChoice == -1) {
                    return false;
                }
                if (hhChoice == 1) {
                    controller.activateHappyHour();
                    return true;
                } else if (hhChoice == 2) {
                    return true;
                } else {
                    System.out.println("[INVALID] Choice must be 1 (Yes) or 2 (No).");
                }
            }
        }
        return true;
    }

    /**
     * Collects the manager's choice for the Host role (Stage 3).
     *
     * @return true to continue, false if input stream terminated.
     */
    public boolean handleHostAction(RestaurantController controller, int turn) {
        while (true) {
            System.out.println("\n[HOST ACTION] Select Host Task for Turn " + turn + ":");
            System.out.println("1. Seat a customer");
            System.out.println("2. Do Nothing / Wait");
            System.out.print("Host Choice > ");

            int hChoice = getSafeIntInput();
            if (hChoice == -1) {
                return false;
            }

            if (hChoice == 1) {
                if (controller.getWaitingQueue().isEmpty()) {
                    System.out.println("[INVALID] Queue is empty, no customer to seat! Choose another action.");
                    continue;
                }
                System.out.print("Which Table to seat at? (1 or 2, 0 to cancel): ");
                int tInput = getSafeIntInput();
                if (tInput == -1) {
                    return false;
                }
                if (tInput == 0) {
                    System.out.println("[CANCEL] Host action cancelled.");
                    continue;
                }
                int tableIdx = tInput - 1;
                if (controller.hostSeatCustomer(tableIdx)) {
                    return true;
                }
            } else if (hChoice == 2) {
                controller.hostWait();
                System.out.println("[ACTION] Host remained idle / waited this turn.");
                return true;
            } else {
                System.out.println("[INVALID] Choice must be 1 or 2. Please try again.");
            }
        }
    }

    /**
     * Collects the manager's choice for the Waiter role.
     *
     * @return true to continue, false if input stream terminated.
     */
    public boolean handleWaiterAction(RestaurantController controller, int turn) {
        while (true) {
            System.out.println("\n[WAITER ACTION] Select Waiter Task for Turn " + turn + ":");
            System.out.println("1. Seat a customer");
            System.out.println("2. Take Order at a Table");
            System.out.println("3. Serve READY Food to a Table");
            System.out.println("4. Do Nothing / Wait");
            System.out.print("Waiter Choice > ");

            int choice = getSafeIntInput();
            if (choice == -1) {
                return false;
            }

            if (choice == 1) {
                if (controller.getWaitingQueue().isEmpty()) {
                    System.out.println("[INVALID] Queue is empty, no customer to seat! Choose another action.");
                    continue;
                }
                System.out.print("Which Table to seat at? (1 or 2, 0 to cancel): ");
                int tInput = getSafeIntInput();
                if (tInput == -1) {
                    return false;
                }
                if (tInput == 0) {
                    System.out.println("[CANCEL] Waiter action cancelled.");
                    continue;
                }
                int tableIdx = tInput - 1;
                if (controller.seatCustomer(tableIdx)) {
                    return true;
                }
            } else if (choice == 2) {
                System.out.print("Which Table to take order for? (1 or 2, 0 to cancel): ");
                int tInput = getSafeIntInput();
                if (tInput == -1) {
                    return false;
                }
                if (tInput == 0) {
                    System.out.println("[CANCEL] Waiter action cancelled.");
                    continue;
                }
                int tableIdx = tInput - 1;
                if (controller.takeOrder(tableIdx)) {
                    return true;
                }
            } else if (choice == 3) {
                System.out.print("Which Table to serve READY food to? (1 or 2, 0 to cancel): ");
                int tInput = getSafeIntInput();
                if (tInput == -1) {
                    return false;
                }
                if (tInput == 0) {
                    System.out.println("[CANCEL] Waiter action cancelled.");
                    continue;
                }
                int tableIdx = tInput - 1;
                if (controller.serveFood(tableIdx)) {
                    return true;
                }
            } else if (choice == 4) {
                controller.waiterWait();
                System.out.println("[ACTION] Waiter remained idle / waited this turn.");
                return true;
            } else {
                System.out.println("[INVALID] Choice must be between 1 and 4. Please try again.");
            }
        }
    }

    /**
     * Collects the manager's choice for the Chef role.
     *
     * @return true to continue, false if input stream terminated.
     */
    public boolean handleChefAction(RestaurantController controller, int turn) {
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

        while (true) {
            System.out.println("\n[CHEF ACTION] Select Chef Task for Turn " + turn + ":");
            if (cookableTables.isEmpty()) {
                System.out.println("No pending orders need cooking. Chef must wait.");
                System.out.println("1. Do Nothing / Wait");
                System.out.print("Chef Choice > ");
                int cChoice = getSafeIntInput();
                if (cChoice == -1) {
                    return false;
                }
                if (cChoice == 1) {
                    controller.chefWait();
                    System.out.println("[ACTION] Chef waited this turn.");
                    return true;
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
                int cChoice = getSafeIntInput();
                if (cChoice == -1) {
                    return false;
                }
                if (cChoice == 1) {
                    if (controller.cookDish(targetTableIdx)) {
                        return true;
                    }
                } else if (cChoice == 2) {
                    controller.chefWait();
                    System.out.println("[ACTION] Chef waited this turn.");
                    return true;
                } else {
                    System.out.println("[INVALID] Choice must be 1 or 2.");
                }
            } else {
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
                int cChoice = getSafeIntInput();
                if (cChoice == -1) {
                    return false;
                }
                if (cChoice == 1) {
                    if (controller.cookDish(tIdx1)) {
                        return true;
                    }
                } else if (cChoice == 2) {
                    if (controller.cookDish(tIdx2)) {
                        return true;
                    }
                } else if (cChoice == 3) {
                    controller.chefWait();
                    System.out.println("[ACTION] Chef waited this turn.");
                    return true;
                } else {
                    System.out.println("[INVALID] Choice must be 1, 2, or 3.");
                }
            }
        }
    }

    public void close() {
        scanner.close();
    }

    // ==========================================
    // INTERNAL HELPERS
    // ==========================================

    private int getSafeIntInput() {
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

    private static String formatQueueNames(List<Customer> queue) {
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
}
