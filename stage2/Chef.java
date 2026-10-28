public class Chef {
    private boolean actedThisTurn = false;

    public boolean hasActed() {
        return actedThisTurn;
    }

    public void resetTurn() {
        this.actedThisTurn = false;
    }

    public void waitTurn() {
        this.actedThisTurn = true;
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

    // Direct helper method if cooking an order directly
    public void prepare(Order order) {
        if (order != null) {
            order.advancePreparation();
            this.actedThisTurn = true;
        }
    }
}
