public class Waiter {
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

    public boolean seat(Customer customer, Table table) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Waiter has already taken an action this turn.");
            return false;
        }
        if (customer == null) {
            System.out.println("[DENIED] No customer to seat.");
            return false;
        }
        if (table == null) {
            System.out.println("[DENIED] Invalid table.");
            return false;
        }
        if (table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is already occupied by: "
                    + table.getseatedCustomer().getName());
            return false;
        }

        table.seat(customer);
        customer.setState(CustomerState.SEATED);
        this.actedThisTurn = true;
        System.out.println("[ACTION] Waiter seated " + customer.getName() + " at Table " + table.getTableNumber());
        return true;
    }

    public boolean takeOrder(Table table, int menuChoice) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Waiter has already taken an action this turn.");
            return false;
        }
        if (table == null) {
            System.out.println("[DENIED] Invalid table.");
            return false;
        }
        if (!table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is empty! Seat a customer first.");
            return false;
        }
        Customer customer = table.getseatedCustomer();
        if (customer.getOrder() != null) {
            System.out.println("[DENIED] Customer at Table " + table.getTableNumber() + " already ordered.");
            return false;
        }

        Order order = new Order(menuChoice);
        customer.setOrder(order);
        customer.setState(CustomerState.ORDERED);
        this.actedThisTurn = true;
        System.out.println("[ACTION] Took order: " + customer.getName() + " chose " + order.getFoodName()
                + " ($" + String.format("%.2f", order.getPrice()) + ", " + order.getPrepUnitsLeft()
                + " prep unit(s)) at Table " + table.getTableNumber());
        return true;
    }

    public boolean serve(Table table) {
        if (actedThisTurn) {
            System.out.println("[DENIED] Waiter has already taken an action this turn.");
            return false;
        }
        if (table == null) {
            System.out.println("[DENIED] Invalid table.");
            return false;
        }
        if (!table.isOccupied()) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " is empty!");
            return false;
        }
        Customer customer = table.getseatedCustomer();
        Order order = customer.getOrder();
        if (order == null) {
            System.out.println("[DENIED] Table " + table.getTableNumber() + " has not ordered yet!");
            return false;
        }
        if (!order.isReady()) {
            System.out.println("[DENIED] Food is not READY yet (" + order.getPrepUnitsLeft() + " prep unit(s) left)!");
            return false;
        }
        if (order.isServed()) {
            System.out.println("[DENIED] Food is already served at Table " + table.getTableNumber());
            return false;
        }

        order.serve();
        customer.setState(CustomerState.SERVED);
        this.actedThisTurn = true;
        System.out.println("[ACTION] Waiter served " + order.getFoodName() + " to " + customer.getName()
                + " at Table " + table.getTableNumber());
        return true;
    }
}
