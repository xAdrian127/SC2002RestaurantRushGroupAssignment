package entity.customer;

/**
 * A demanding critic. The critic loses 12 satisfaction points per eligible
 * turn, but becomes unforgiving below 50 satisfaction and then loses 17.
 */
public class CriticCustomer extends Customer {
    public CriticCustomer(String name) {
        super(name);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        // Loses 12 satisfaction points per eligible turn, but becomes unforgiving below 50 and loses 17
        return getSatisfaction() < 50 ? 17 : 12;
    }

    @Override
    public double calculateBill() {
        return super.calculateBill(); // Full price
    }

    @Override
    public int chooseMenuChoice() {
        // Documented Critic distinct order preference (Section 4.2):
        // The critic always orders Pasta (Choice 3, 2 prep units, $18.00)
        // to evaluate the kitchen's technical preparation complexity.
        return 3;
    }
}

