package entity.customer;

/**
 * A standard customer who loses 8 satisfaction points per eligible turn
 * and pays full bill price.
 */
public class RegularCustomer extends Customer {
    public RegularCustomer(String name) {
        super(name);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return 8;
    }

    @Override
    public double calculateBill() {
        return super.calculateBill();
    }

    @Override
    public int chooseMenuChoice() {
        // Documented deterministic preference for base runs:
        // Alice prefers Burger (Choice 2), David prefers Salad (Choice 1)
        if ("Alice".equalsIgnoreCase(getName())) {
            return 2; // Burger ($15.00, 1 prep unit)
        }
        if ("David".equalsIgnoreCase(getName())) {
            return 1; // Salad ($9.00, 1 prep unit)
        }
        return super.chooseMenuChoice();
    }
}

