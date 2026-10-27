import java.util.ArrayList;
import java.util.List;

public class CustomerRules {
    public static final int REGULAR_LOSS_PER_TURN = 8;

    public int lossFor(Customer customer) {
        return hasBeenServed(customer) ? 0 : REGULAR_LOSS_PER_TURN;
    }

    public boolean applyWaitingTurn(Customer customer) {
        int loss = lossFor(customer);
        customer.setSatisfaction(Math.max(0, customer.getSatisfaction() - loss));
        return customer.getSatisfaction() <= 0;
    }

    public List<Customer> applyWaitingTurn(List<Customer> queue) {
        List<Customer> departed = new ArrayList<>();
        for (Customer c : new ArrayList<>(queue)) {
            if (applyWaitingTurn(c)) {
                queue.remove(c);
                departed.add(c);
            }
        }
        return departed;
    }

    public Customer applyWaitingTurn(Table table) {
        Customer c = table.getOccupiedBy();
        if (c != null && applyWaitingTurn(c)) {
            table.clearTable();
            return c;
        }
        return null;
    }

    public CustomerStatus statusOf(Customer customer, boolean seated) {
        Order o = customer.getOrder();
        if (!seated) return CustomerStatus.IN_QUEUE;
        if (o == null) return CustomerStatus.SEATED;
        if (o.isReadyToPay()) return CustomerStatus.READY_TO_PAY;
        if (o.isServed()) return CustomerStatus.EATING;
        if (o.isReady()) return CustomerStatus.READY;
        return CustomerStatus.COOKING;
    }

    private boolean hasBeenServed(Customer customer) {
        Order o = customer.getOrder();
        return o != null && o.isServed();
    }
}
