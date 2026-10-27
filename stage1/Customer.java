public class Customer {
    public static final int MAX_SATISFACTION = 100;
    public static final int WAITING_LOSS = 8;

    private final String name;
    private int satisfaction;
    private CustomerState state;
    private Order order;

    public Customer(String name) {
        this.name = name;
        this.satisfaction = MAX_SATISFACTION;
        this.state = CustomerState.QUEUED;
    }

    public void decreaseSatisfaction(int points) {
        if (state == CustomerState.QUEUED || state == CustomerState.SEATED || state == CustomerState.ORDERED) {
            this.satisfaction = Math.max(0, this.satisfaction - points);
            if (this.satisfaction == 0) {
                this.state = CustomerState.LEFT_UNHAPPY;
            }
        }
    }

    public void decreaseSatisfaction() {
        decreaseSatisfaction(WAITING_LOSS);
    }

    public int calculateBill(int baseCents) {
        return baseCents;
    }

    public boolean isAbandoned() {
        return this.state == CustomerState.LEFT_UNHAPPY;
    }

    public String getName() {
        return name;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = Math.max(0, Math.min(MAX_SATISFACTION, satisfaction));
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
