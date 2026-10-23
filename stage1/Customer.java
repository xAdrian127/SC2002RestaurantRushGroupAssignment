public class Customer {
    private String name;
    private int satisfaction;
    private Order order;

    public Customer(String name) {
        this.name = name;
        this.satisfaction = 100; // Starting satisfaction for Stage 1
    }

    // Getters and Setters
    public String getName() {
        return name;
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
