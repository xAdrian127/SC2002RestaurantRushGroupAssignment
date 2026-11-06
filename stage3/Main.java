import boundary.RestaurantUI;
import control.RestaurantController;

/**
 * Application Entrypoint.
 * Adheres strictly to the Boundary-Control-Entity (BCE) architectural pattern.
 * Main delegates all presentation, display formatting, and user input collection
 * to the boundary layer (RestaurantUI), and delegates turn simulation rules to the controller layer.
 */
public class Main {
    public static void main(String[] args) {
        RestaurantController controller = new RestaurantController();
        RestaurantUI ui = new RestaurantUI();

        // 1. Application Welcome Banner
        ui.displayWelcomeBanner();

        // 2. 18-Turn Main Execution Loop
        for (int turn = 1; turn <= 18; turn++) {
            // Step 1: Start-of-Turn Engine & Live Status Display
            controller.processTurnStart(turn);
            ui.displayTurnStatus(controller);

            // Step 1B: Optional Dynamic Policy Activation (Happy Hour)
            if (!ui.handleHappyHourPrompt(controller, turn)) {
                return; // Input stream closed
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
            controller.processTurnEnd();

            // End-of-Turn Metric Display
            ui.displayTurnSummary(controller, turn);
        }

        // Step 6: End Game Evaluation & Reporting
        ui.displayEndGame(controller);
        ui.close();
    }
}
