package entity.customer;

/**
 * A standard customer who loses 8 satisfaction points per eligible turn
 * and pays full bill price (subject to Happy Hour policy if active at order time).
 */
public class RegularCustomer extends Customer {
    public RegularCustomer(String name, int arrivalNumber) {
        super(name, arrivalNumber);
    }

    public RegularCustomer(String name) {
        super(name);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return 8;
    }

    @Override
    public String getCustomerType() {
        return "RegularCustomer";
    }

    @Override
    public int chooseMenuChoice() {
        // Documented deterministic preference by arrival sequence (Section 2.2 & 4.3):
        // Odd arrival index (e.g. #1) orders Burger (Choice 2, $15.00, 1 prep unit);
        // Even arrival index (e.g. #4) orders Salad + Pasta Combo (Choice 4, $24.30, 3 prep units).
        return (getArrivalNumber() % 2 != 0) ? 2 : 4;
    }
}
