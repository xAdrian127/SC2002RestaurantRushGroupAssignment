package entity;

public class Order {
    private final MenuItem menuItem;
    private int prepUnitsLeft;
    private boolean isReady;
    private boolean isServed;
    private boolean isPaid;

    public Order(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null for an Order");
        }
        this.menuItem = menuItem;
        this.prepUnitsLeft = menuItem.getPreparationUnits();
        this.isReady = (this.prepUnitsLeft <= 0);
        this.isServed = false;
        this.isPaid = false;
    }

    public Order(int choice) {
        this(Menu.Choice(choice));
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public String getFoodName() {
        return menuItem.getName();
    }

    public double getPrice() {
        return menuItem.getPriceCents() / 100.0;
    }

    public int getPriceCents() {
        return menuItem.getPriceCents();
    }

    public int getPrepUnitsLeft() {
        return this.prepUnitsLeft;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        this.isReady = ready;
    }

    public boolean isServed() {
        return isServed;
    }

    public void setServed(boolean served) {
        this.isServed = served;
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

    public boolean isPaid() {
        return isPaid;
    }

    public void markPaid() {
        this.isPaid = true;
    }
}

