public class Customer {
    private final String name;
    private int satisfaction;
    private CustomerState state;
    private Order order;

    public Customer(String name) {
        this.name = name;
        this.satisfaction = 100; // Starting satisfaction for Stage 1
        this.state = CustomerState.QUEUED;
    }

    public void decreaseSatisfication(int points) {
        // Only queued, seated, or ordered customers lose satisfaction
        if (state == CustomerState.QUEUED ||
                state == CustomerState.SEATED ||
                state == CustomerState.ORDERED) {
            this.satisfaction = Math.max(0, this.satisfaction - points);
            // Auto-transition state if satisfaction reaches 0
            if (this.satisfaction == 0) {
                this.state = CustomerState.LEFT_UNHAPPY;
            }
        }
    }

    // check the state of customer == left unhappy
    public boolean abandonment() {
        return this.state == CustomerState.LEFT_UNHAPPY;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
