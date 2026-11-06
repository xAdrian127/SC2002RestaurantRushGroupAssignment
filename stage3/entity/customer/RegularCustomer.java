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
    public int chooseMenuChoice() {
        // Documented deterministic preference:
        // Alice prefers Burger (Choice 2, $15.00, 1 prep unit)
        // David prefers a ComboMeal (Choice 4: Salad + Pasta Combo, $24.30, 3 prep units)
        if ("Alice".equalsIgnoreCase(getName())) {
            return 2; // Burger
        }
        if ("David".equalsIgnoreCase(getName())) {
            return 4; // Salad + Pasta Combo (Section 4.3 requirement)
        }
        return super.chooseMenuChoice();
    }
}
