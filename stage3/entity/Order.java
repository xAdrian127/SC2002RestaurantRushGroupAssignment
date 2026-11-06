package entity;

/**
 * Encapsulates an individual food order.
 * Stores preparation units, dish status (READY, SERVED, PAID), and the locked price in cents.
 * Fulfills Section 4.3 rule: Price is locked when the order is placed and does not change retroactively.
 */
public class Order {
    private final MenuItem menuItem;
    private final int lockedPriceCents;
    private int prepUnitsLeft;
    private boolean isReady;
    private boolean isServed;
    private boolean isPaid;

    public Order(MenuItem menuItem, int lockedPriceCents) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null for an Order");
        }
        if (lockedPriceCents < 0) {
            throw new IllegalArgumentException("Locked price cannot be negative");
        }
        this.menuItem = menuItem;
        this.lockedPriceCents = lockedPriceCents;
        this.prepUnitsLeft = menuItem.getPreparationUnits();
        this.isReady = (this.prepUnitsLeft <= 0);
        this.isServed = false;
        this.isPaid = false;
    }

    public Order(MenuItem menuItem) {
        this(menuItem, menuItem.getPriceCents());
    }

    public Order(int choice) {
        this(Menu.getItem(choice));
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public String getFoodName() {
        return menuItem.getName();
    }

    public double getPrice() {
        return lockedPriceCents / 100.0;
    }

    public int getPriceCents() {
        return lockedPriceCents;
    }

    public int getPrepUnitsLeft() {
        return this.prepUnitsLeft;
    }

    public boolean isReady() {
        return isReady;
    }

    public boolean isServed() {
        return isServed;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void advancePreparation() {
        if (!isReady && prepUnitsLeft > 0) {
            prepUnitsLeft--;
            if (prepUnitsLeft == 0) {
                isReady = true;
            }
        }
    }

    public void serve() {
        if (isReady && !isServed) {
            isServed = true;
        }
    }

    public void markPaid() {
        this.isPaid = true;
    }
}
