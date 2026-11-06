package boundary;

import control.ActionResult;
import control.RestaurantController;
import entity.Order;
import entity.Table;

import java.util.List;

/**
 * Boundary component responsible strictly for interactive manager command menus (SRP).
 * Handles Happy Hour activation prompts and Host, Waiter, and Chef task menus,
 * delegating console input to InputReader and output rendering to GameStatusView.
 */
public class StaffActionMenu {
    private final InputReader inputReader;
    private final GameStatusView statusView;

    public StaffActionMenu(InputReader inputReader, GameStatusView statusView) {
        if (inputReader == null || statusView == null) {
            throw new IllegalArgumentException("InputReader and GameStatusView cannot be null");
        }
        this.inputReader = inputReader;
        this.statusView = statusView;
    }

    /**
     * Prompts the player to optionally activate Happy Hour at turn start (once per game).
     *
     * @return true to continue, false if input stream terminated.
     */
    public boolean handleHappyHourPrompt(RestaurantController controller, int turn) {
        if (!controller.isHappyHourActivated()) {
            statusView.printLine("\n[HAPPY HOUR POLICY] Happy Hour is available (Activate once per game; lasts 2 turns with 20% order discount & +15 sat boost).");
            while (true) {
                Integer hhChoice = inputReader.promptInt("Activate Happy Hour for Turn " + turn + "? (1 = Yes, 2 = No): ");
                if (hhChoice == null) {
                    return false;
                }
                if (hhChoice == 1) {
                    statusView.printActionResult(controller.activateHappyHour());
                    return true;
                } else if (hhChoice == 2) {
                    return true;
                } else {
                    statusView.printLine("[INVALID] Choice must be 1 (Yes) or 2 (No).");
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
            statusView.printLine("\n[HOST ACTION] Select Host Task for Turn " + turn + ":");
            statusView.printLine("1. Seat a customer");
            statusView.printLine("2. Do Nothing / Wait");

            Integer hChoice = inputReader.promptInt("Host Choice > ");
            if (hChoice == null) {
                return false;
            }

            if (hChoice == 1) {
                if (controller.isWaitingQueueEmpty()) {
                    statusView.printLine("[INVALID] Queue is empty, no customer to seat! Choose another action.");
                    continue;
                }
                Integer tableNum = promptTableSelection(
                        "Which Table to seat at", controller.getTableCount(), "Host");
                if (tableNum == null) {
                    return false;
                }
                if (tableNum == 0) {
                    continue;
                }
                ActionResult result = controller.hostSeatCustomer(tableNum);
                statusView.printActionResult(result);
                if (result.isSuccess()) {
                    return true;
                }
            } else if (hChoice == 2) {
                statusView.printActionResult(controller.hostWait());
                return true;
            } else {
                statusView.printLine("[INVALID] Choice must be 1 or 2. Please try again.");
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
            statusView.printLine("\n[WAITER ACTION] Select Waiter Task for Turn " + turn + ":");
            statusView.printLine("1. Seat a customer");
            statusView.printLine("2. Take Order at a Table");
            statusView.printLine("3. Serve READY Food to a Table");
            statusView.printLine("4. Do Nothing / Wait");

            Integer choice = inputReader.promptInt("Waiter Choice > ");
            if (choice == null) {
                return false;
            }

            if (choice == 1) {
                if (controller.isWaitingQueueEmpty()) {
                    statusView.printLine("[INVALID] Queue is empty, no customer to seat! Choose another action.");
                    continue;
                }
                Integer tableNum = promptTableSelection(
                        "Which Table to seat at", controller.getTableCount(), "Waiter");
                if (tableNum == null) {
                    return false;
                }
                if (tableNum == 0) {
                    continue;
                }
                ActionResult result = controller.seatCustomer(tableNum);
                statusView.printActionResult(result);
                if (result.isSuccess()) {
                    return true;
                }
            } else if (choice == 2) {
                Integer tableNum = promptTableSelection(
                        "Which Table to take order for", controller.getTableCount(), "Waiter");
                if (tableNum == null) {
                    return false;
                }
                if (tableNum == 0) {
                    continue;
                }
                ActionResult result = controller.takeOrder(tableNum);
                statusView.printActionResult(result);
                if (result.isSuccess()) {
                    return true;
                }
            } else if (choice == 3) {
                Integer tableNum = promptTableSelection(
                        "Which Table to serve READY food to", controller.getTableCount(), "Waiter");
                if (tableNum == null) {
                    return false;
                }
                if (tableNum == 0) {
                    continue;
                }
                ActionResult result = controller.serveFood(tableNum);
                statusView.printActionResult(result);
                if (result.isSuccess()) {
                    return true;
                }
            } else if (choice == 4) {
                statusView.printActionResult(controller.waiterWait());
                return true;
            } else {
                statusView.printLine("[INVALID] Choice must be between 1 and 4. Please try again.");
            }
        }
    }

    /**
     * Collects the manager's choice for the Chef role dynamically across any number of tables (OCP).
     *
     * @return true to continue, false if input stream terminated.
     */
    public boolean handleChefAction(RestaurantController controller, int turn) {
        List<Table> cookableTables = controller.getCookableTables();
        int waitOption = cookableTables.size() + 1;

        while (true) {
            statusView.printLine("\n[CHEF ACTION] Select Chef Task for Turn " + turn + ":");
            if (cookableTables.isEmpty()) {
                statusView.printLine("No pending orders need cooking. Chef must wait.");
            } else {
                for (int i = 0; i < cookableTables.size(); i++) {
                    Table table = cookableTables.get(i);
                    Order order = table.getActiveOrder();
                    statusView.printLine((i + 1) + ". Cook order for Table " + table.getTableNumber()
                            + " (" + order.getFoodName() + ", " + order.getPrepUnitsLeft() + " prep unit(s) left)");
                }
            }
            statusView.printLine(waitOption + ". Do Nothing / Wait");

            Integer cChoice = inputReader.promptInt("Chef Choice > ");
            if (cChoice == null) {
                return false;
            }

            if (cChoice >= 1 && cChoice <= cookableTables.size()) {
                Table selectedTable = cookableTables.get(cChoice - 1);
                ActionResult result = controller.cookDish(selectedTable.getTableNumber());
                statusView.printActionResult(result);
                if (result.isSuccess()) {
                    return true;
                }
            } else if (cChoice == waitOption) {
                statusView.printActionResult(controller.chefWait());
                return true;
            } else {
                statusView.printLine("[INVALID] Choice must be between 1 and " + waitOption + ".");
            }
        }
    }

    /**
     * Shared helper prompting for a 1-based table number with 0-to-cancel support (DRY).
     *
     * @return Selected table number, 0 if cancelled, or null on EOF.
     */
    private Integer promptTableSelection(String promptPrefix, int tableCount, String roleName) {
        Integer tableNum = inputReader.promptInt(promptPrefix + "? (1 to " + tableCount + ", 0 to cancel): ");
        if (tableNum == null) {
            return null;
        }
        if (tableNum == 0) {
            statusView.printLine("[CANCEL] " + roleName + " action cancelled.");
        }
        return tableNum;
    }
}
