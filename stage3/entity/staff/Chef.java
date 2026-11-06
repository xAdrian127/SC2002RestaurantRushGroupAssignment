package entity.staff;

import entity.Order;
import entity.Table;
import entity.customer.Customer;

/**
 * Chef staff role.
 * Advances one preparation unit per turn for a chosen pending order.
 * Extends Staff to eliminate duplicate state and uphold LSP.
 */
public class Chef extends Staff {

    @Override
    public String getRoleName() {
        return "Chef";
    }

    public boolean cook(Table table) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Chef has already taken an action this turn.");
            return false;
        }
        if (table == null || !table.isOccupied()) {
            System.out.println("[DENIED] Table is not occupied.");
            return false;
        }

        Customer customer = table.getseatedCustomer();
        Order order = customer.getOrder();
        if (order == null) {
            System.out.println("[DENIED] No active order at Table " + table.getTableNumber());
            return false;
        }
        if (order.isReady()) {
            System.out.println("[DENIED] Dish at Table " + table.getTableNumber() + " is already READY!");
            return false;
        }

        order.advancePreparation();
        this.actedThisTurn = true;
        System.out.println("[CHEF] Cooked 1 unit for " + customer.getName() + "'s " + order.getFoodName()
                + " (" + order.getPrepUnitsLeft() + " prep unit(s) left)");
        if (order.isReady()) {
            System.out.println("[CHEF] " + order.getFoodName() + " for " + customer.getName()
                    + " is now READY! (Can be served next turn)");
        }
        return true;
    }

    public void prepare(Order order) {
        if (order != null) {
            order.advancePreparation();
            this.actedThisTurn = true;
        }
    }
}
