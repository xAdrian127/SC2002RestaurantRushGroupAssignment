package boundary;

import control.RestaurantController;

import java.util.List;

/**
 * Top-level Boundary Facade (BCE Architecture & Single Responsibility Principle).
 * Mirrors RestaurantController on the boundary side by coordinating specialized boundary components:
 * - InputReader: Low-level Scanner lifecycle, EOF handling, and numeric token validation.
 * - GameStatusView: Read-only CLI status rendering, event logs, and victory/defeat scoreboards.
 * - StaffActionMenu: Interactive manager menus for Happy Hour, Host, Waiter, and Chef actions.
 */
public class RestaurantUI {
    private final InputReader inputReader;
    private final GameStatusView statusView;
    private final StaffActionMenu actionMenu;

    public RestaurantUI(InputReader inputReader, GameStatusView statusView) {
        if (inputReader == null || statusView == null) {
            throw new IllegalArgumentException("InputReader and GameStatusView cannot be null");
        }
        this.inputReader = inputReader;
        this.statusView = statusView;
        this.actionMenu = new StaffActionMenu(this.inputReader, this.statusView);
    }

    public RestaurantUI() {
        this(new InputReader(), new GameStatusView());
    }

    public void displayWelcomeBanner() {
        statusView.displayWelcomeBanner();
    }

    public void displayTurnStart(RestaurantController controller, int turn, List<String> startEvents) {
        statusView.displayTurnStart(controller, turn, startEvents);
    }

    public void displayTurnEnd(RestaurantController controller, int turn, List<String> endEvents) {
        statusView.displayTurnEnd(controller, turn, endEvents);
    }

    public void displayEndGame(RestaurantController controller) {
        statusView.displayEndGame(controller);
    }

    public boolean handleHappyHourPrompt(RestaurantController controller, int turn) {
        return actionMenu.handleHappyHourPrompt(controller, turn);
    }

    public boolean handleHostAction(RestaurantController controller, int turn) {
        return actionMenu.handleHostAction(controller, turn);
    }

    public boolean handleWaiterAction(RestaurantController controller, int turn) {
        return actionMenu.handleWaiterAction(controller, turn);
    }

    public boolean handleChefAction(RestaurantController controller, int turn) {
        return actionMenu.handleChefAction(controller, turn);
    }

    public void close() {
        inputReader.close();
    }
}
