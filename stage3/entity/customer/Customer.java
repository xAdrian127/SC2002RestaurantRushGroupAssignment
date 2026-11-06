package entity.customer;

import entity.Menu;
import entity.Order;

public class Customer {
    private final String name;
    private int satisfaction;
    private CustomerState state;
    private Order order;
    private int turnBecameReadyToPay = -1;

    public Customer(String name) {
        this.name = name;
        this.satisfaction = 100;
        this.state = CustomerState.QUEUED;
    }

    /**
     * Polymorphic satisfaction decay rate per unserved waiting turn.
     * Subclasses override this to implement specialized customer decay rates.
     */
    public int getSatisfactionLossPerTurn() {
        return 8; // Default loss for regular customers
    }

    /**
     * Polymorphically decreases satisfaction based on the customer's decay rate.
     */
    public void decaySatisfaction() {
        decreaseSatisfaction(getSatisfactionLossPerTurn());
    }

    public void decreaseSatisfaction(int points) {
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

    // Check if customer left unhappy
    public boolean abandonment() {
        return this.state == CustomerState.LEFT_UNHAPPY;
    }

    /**
     * Polymorphic bill calculation in integer cents.
     * Stored in cents to avoid fractional floating-point rounding issues (Section 2.2).
     * Subclasses override this to apply specialized pricing (e.g. VIP 10% discount).
     */
    public int calculateBillCents() {
        return order != null ? order.getPriceCents() : 0;
    }

    /**
     * Polymorphic bill calculation in dollars.
     */
    public double calculateBill() {
        return calculateBillCents() / 100.0;
    }

    /**
     * Polymorphic food selection logic. Keeps domain rules encapsulated inside
     * customer objects rather than leaking into the UI.
     * Uses a documented deterministic preference to ensure reproducible runs (Section 2.2).
     */
    public int chooseMenuChoice() {
        return (Math.abs(name.hashCode()) % Menu.getItemCount()) + 1;
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

    public int getTurnBecameReadyToPay() {
        return turnBecameReadyToPay;
    }

    public void setTurnBecameReadyToPay(int turnBecameReadyToPay) {
        this.turnBecameReadyToPay = turnBecameReadyToPay;
    }
}
