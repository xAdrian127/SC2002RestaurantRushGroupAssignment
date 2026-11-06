package boundary;

import control.ActionResult;
import control.GameScoreManager;
import control.RestaurantController;
import entity.Order;
import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

import java.util.List;

/**
 * Boundary component responsible strictly for read-only CLI rendering and formatting (SRP).
 * Displays banners, turn headers, domain event logs, queue/table statuses, and scoreboards.
 */
public class GameStatusView {

    public void displayWelcomeBanner() {
        System.out.println("==========================================");
        System.out.println("             RESTAURANT RUSH              ");
        System.out.println("==========================================");
    }

    public void displayTurnStart(RestaurantController controller, int turn, List<String> startEvents) {
        System.out.println("\n==========================================");
        System.out.println(">>> TURN " + turn + " of " + controller.getMaxTurns() + " <<<");
        if (controller.isHappyHourActive()) {
            System.out.println("[POLICY] Happy Hour is ACTIVE! Remaining Turns: "
                    + controller.getHappyHourTurnsRemaining() + " | 20% discount on new orders");
        }
        System.out.println("==========================================");

        printMessages(startEvents);

        System.out.println("Revenue: $" + String.format("%.2f", controller.getTotalRevenue())
                + " | Paid: " + controller.getPaidCustomerCount()
                + " | Avg Sat: " + controller.getAverageSatisfactionString());
        System.out.println("Waiting Queue (" + controller.getWaitingQueue().size() + "): "
                + formatQueueNames(controller.getWaitingQueue()));
        printTableStatus(controller.getTables());
    }

    public void displayTurnEnd(RestaurantController controller, int turn, List<String> endEvents) {
        printMessages(endEvents);

        System.out.println("\n--- End of Turn " + turn + " Summary ---");
        System.out.println("Served Count               : " + controller.getServedCustomerCount());
        System.out.println("Unhappy Departures         : " + controller.getUnhappyDepartures());
        System.out.println("Revenue                    : $" + String.format("%.2f", controller.getTotalRevenue()));
        System.out.println("Paid Customers             : " + controller.getPaidCustomerCount());
        System.out.println("Average Paid Satisfaction  : " + controller.getAverageSatisfactionString());
        System.out.println("Turns Used                 : " + turn + " / " + controller.getMaxTurns());
    }

    public void displayEndGame(RestaurantController controller) {
        boolean targetCustomersMet = controller.isTargetCustomersMet();
        boolean targetRevenueMet = controller.isTargetRevenueMet();
        boolean targetSatisfactionMet = controller.isTargetSatisfactionMet();
        boolean isVictory = controller.isVictory();

        System.out.println("\n==========================================");
        System.out.println("       GAME OVER - FINAL RESULTS          ");
        System.out.println("==========================================");
        System.out.println("Turns Used                 : " + controller.getMaxTurns() + " / " + controller.getMaxTurns());
        System.out.println("Served Count               : " + controller.getServedCustomerCount());
        System.out.println("Paid Customers             : " + controller.getPaidCustomerCount()
                + " (Target: >= " + GameScoreManager.TARGET_PAID_CUSTOMERS + ") -> "
                + (targetCustomersMet ? "PASSED" : "FAILED"));
        System.out.println("Unhappy Departures         : " + controller.getUnhappyDepartures());
        System.out.println("Total Revenue              : $" + String.format("%.2f", controller.getTotalRevenue())
                + " (Target: >= $" + String.format("%.2f", GameScoreManager.TARGET_REVENUE_CENTS / 100.0) + ") -> "
                + (targetRevenueMet ? "PASSED" : "FAILED"));
        System.out.println("Average Paid Satisfaction  : " + controller.getAverageSatisfactionString()
                + " (Target: >= " + String.format("%.1f", GameScoreManager.TARGET_AVERAGE_SATISFACTION) + ") -> "
                + (targetSatisfactionMet ? "PASSED" : "FAILED"));
        System.out.println("------------------------------------------");
        System.out.println("OVERALL RESULT             : " + (isVictory ? "VICTORY!" : "DEFEAT!"));
        System.out.println("==========================================");
    }

    public void printActionResult(ActionResult result) {
        if (result != null) {
            printMessages(result.getMessages());
        }
    }

    public void printMessages(List<String> messages) {
        if (messages == null) {
            return;
        }
        for (String message : messages) {
            System.out.println(message);
        }
    }

    public void printLine(String message) {
        System.out.println(message);
    }

    private String formatQueueNames(List<Customer> queue) {
        if (queue.isEmpty()) {
            return "Empty";
        }
        StringBuilder sb = new StringBuilder();
        for (Customer customer : queue) {
            sb.append(customer.getName()).append("(").append(customer.getCustomerType())
                    .append(", Sat:").append(customer.getSatisfaction()).append(") ");
        }
        return sb.toString();
    }

    private void printTableStatus(List<Table> tables) {
        for (Table table : tables) {
            if (!table.isOccupied()) {
                System.out.println("Table " + table.getTableNumber() + ": [EMPTY]");
            } else {
                Customer customer = table.getSeatedCustomer();
                Order order = table.getActiveOrder();
                String orderInfo = formatOrderStatus(customer, order);
                System.out.println("Table " + table.getTableNumber() + ": [" + customer.getName()
                        + " (" + customer.getCustomerType() + ") | Sat: " + customer.getSatisfaction()
                        + " | Order: " + orderInfo + "]");
            }
        }
    }

    private String formatOrderStatus(Customer customer, Order order) {
        if (order == null) {
            return "No Order";
        }
        if (customer.getState() == CustomerState.READY_TO_PAY) {
            return order.getFoodName() + " [READY TO PAY]";
        }
        if (customer.getState() == CustomerState.SERVED || order.isServed()) {
            return order.getFoodName() + " [EATING]";
        }
        if (order.isReady()) {
            return order.getFoodName() + " [READY TO SERVE]";
        }
        return order.getFoodName() + " (PrepLeft: " + order.getPrepUnitsLeft() + ")";
    }
}
