import boundary.RestaurantUI;
import control.RestaurantController;

import java.util.List;

/**
 * Application Entrypoint.
 * Adheres strictly to the Boundary-Control-Entity (BCE) architectural pattern:
 * Main wires and coordinates the Boundary layer (RestaurantUI) and Control layer (RestaurantController)
 * without performing any console I/O or holding domain rules.
 */
public class Main {
    public static void main(String[] args) {
        RestaurantController controller = new RestaurantController();
        RestaurantUI ui = new RestaurantUI();

        try {
            ui.displayWelcomeBanner();

            for (int turn = 1; turn <= controller.getMaxTurns(); turn++) {
                // Step 1: Start-of-Turn Engine & Live Status Display
                List<String> startEvents = controller.processTurnStart(turn);
                ui.displayTurnStart(controller, turn, startEvents);

                // Step 1B: Optional Dynamic Policy Activation (Happy Hour)
                if (!ui.handleHappyHourPrompt(controller, turn)) {
                    return;
                }

                // Step 2A: Manager Choice - Host (Stage 3 Role)
                if (!ui.handleHostAction(controller, turn)) {
                    return;
                }

                // Step 2B: Manager Choice - Waiter
                if (!ui.handleWaiterAction(controller, turn)) {
                    return;
                }

                // Step 2C: Manager Choice - Chef
                if (!ui.handleChefAction(controller, turn)) {
                    return;
                }

                // Steps 3-5: Turn-End Engine (Decay, Abandonment & Happy Hour Countdown)
                List<String> endEvents = controller.processTurnEnd();
                ui.displayTurnEnd(controller, turn, endEvents);
            }

            // Step 6: End Game Evaluation & Reporting
            ui.displayEndGame(controller);
        } finally {
            ui.close();
        }
    }
}
