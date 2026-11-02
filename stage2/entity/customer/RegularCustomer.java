/** A standard customer who loses 8 satisfaction points per eligible turn. */
public class RegularCustomer extends Customer {
    public RegularCustomer(String name) {
        super(name);
    }

    @Override
    public void decreaseSatisfication(int points) {
        super.decreaseSatisfication(8);
    }
}
