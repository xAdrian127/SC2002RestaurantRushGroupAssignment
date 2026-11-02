/** A VIP customer who loses 5 satisfaction points per eligible turn. */
public class VIPCustomer extends Customer {
    public VIPCustomer(String name) {
        super(name);
    }

    @Override
    public void decreaseSatisfication(int points) {
        super.decreaseSatisfication(5);
    }
}
