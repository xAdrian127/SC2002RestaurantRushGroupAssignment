package entity.customer;

import entity.HappyHour;
import entity.MenuItem;
import entity.Order;

/**
 * Abstract base class representing a restaurant customer (Generalization / Polymorphism).
 * Encapsulates satisfaction lifecycle, deterministic food choice contract, and order-time price locking.
 */
public abstract class Customer {
    private static final int MAX_SATISFACTION = 100;
    private static final int MIN_SATISFACTION = 0;

    private final String name;
    private final int arrivalNumber;
    private int satisfaction;
    private CustomerState state;
    private Order order;
    private int turnBecameReadyToPay = -1;

    protected Customer(String name, int arrivalNumber) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or blank");
        }
        if (arrivalNumber <= 0) {
            throw new IllegalArgumentException("Arrival number must be positive");
        }
        this.name = name;
        this.arrivalNumber = arrivalNumber;
        this.satisfaction = MAX_SATISFACTION;
        this.state = CustomerState.QUEUED;
    }

    protected Customer(String name) {
        this(name, 1);
    }

    /**
     * Polymorphic satisfaction decay rate per unserved waiting turn.
     */
    public abstract int getSatisfactionLossPerTurn();

    /**
     * Polymorphic customer category label for display without reflection.
     */
    public abstract String getCustomerType();

    /**
     * Polymorphic deterministic food selection logic (Section 2.2).
     * Customers select food based on their type and arrival sequence, never by manager input.
     */
    public abstract int chooseMenuChoice();

    /**
     * Polymorphically decreases satisfaction based on the customer's decay rate.
     */
    public void decaySatisfaction() {
        decreaseSatisfaction(getSatisfactionLossPerTurn());
    }

    public void decreaseSatisfaction(int points) {
        if (points <= 0) {
            return;
        }
        if (isWaitingForTableOrFood()) {
            this.satisfaction = Math.max(MIN_SATISFACTION, this.satisfaction - points);
            if (this.satisfaction == MIN_SATISFACTION) {
                this.state = CustomerState.LEFT_UNHAPPY;
            }
        }
    }

    /**
     * Recovers satisfaction during active Happy Hour turns (Section 4.3).
     * Only customers still waiting for a table or food (QUEUED, SEATED, ORDERED) are eligible,
     * capped at 100.
     *
     * @param points Satisfaction points to recover.
     * @return Actual points recovered (0 if ineligible or already at 100).
     */
    public int recoverSatisfaction(int points) {
        if (points <= 0 || !isWaitingForTableOrFood() || this.satisfaction >= MAX_SATISFACTION) {
            return 0;
        }
        int previous = this.satisfaction;
        this.satisfaction = Math.min(MAX_SATISFACTION, this.satisfaction + points);
        return this.satisfaction - previous;
    }

    public boolean isWaitingForTableOrFood() {
        return state == CustomerState.QUEUED
                || state == CustomerState.SEATED
                || state == CustomerState.ORDERED;
    }

    public boolean hasAbandoned() {
        return this.state == CustomerState.LEFT_UNHAPPY;
    }

    public boolean hasOrdered() {
        return this.order != null;
    }

    public boolean hasCookableOrder() {
        return this.order != null && !this.order.isReady();
    }

    /**
     * Polymorphically computes the locked bill in cents when an order is placed,
     * applying customer discounts (e.g. VIP 10%) and dynamic policies (Happy Hour 20%).
     *
     * @param item              The menu item chosen.
     * @param isHappyHourActive Whether Happy Hour is active when order is placed.
     * @return Final locked bill in integer cents.
     */
    public int computeOrderBillCents(MenuItem item, boolean isHappyHourActive) {
        int price = item.getPriceCents();
        if (isHappyHourActive) {
            price = HappyHour.calculateDiscount(price);
        }
        return price;
    }

    /**
     * Returns the locked bill stored in the order in integer cents.
     */
    public int calculateBillCents() {
        return order != null ? order.getPriceCents() : 0;
    }

    /**
     * Returns the locked bill in dollars for display formatting.
     */
    public double calculateBill() {
        return calculateBillCents() / 100.0;
    }

    public String getName() {
        return name;
    }

    public int getArrivalNumber() {
        return arrivalNumber;
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
