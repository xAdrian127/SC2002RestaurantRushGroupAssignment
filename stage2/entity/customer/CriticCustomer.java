/**
 * A demanding critic. The critic loses 12 satisfaction points per eligible
 * turn, but becomes unforgiving below 50 satisfaction and then loses 17.
 */
public class CriticCustomer extends Customer {
    public CriticCustomer(String name) {
        super(name);
    }

    @Override
    public void decreaseSatisfication(int points) {
        int loss = getSatisfaction() < 50 ? 17 : 12;
        super.decreaseSatisfication(loss);
    }
}
