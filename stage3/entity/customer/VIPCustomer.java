package entity.customer;

/**
 * A VIP customer who loses only 5 satisfaction points per eligible turn
 * and receives a 10% discount on their total bill.
 */
public class VIPCustomer extends Customer {
    public VIPCustomer(String name) {
        super(name);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return 5;
    }

    @Override
    public int calculateBillCents() {
        // VIP customers receive a 10% discount, stored in integer cents (Section 2.2)
        return (int) Math.round(super.calculateBillCents() * 0.90);
    }

    @Override
    public double calculateBill() {
        // VIP customers receive a 10% discount, dispatched polymorphically without instanceof checks
        return calculateBillCents() / 100.0;
    }

    @Override
    public int chooseMenuChoice() {
        // Documented VIP order preference: VIPs prefer high-tier dining.
        // Bob prefers Burger (Choice 2), Edward prefers Pasta (Choice 3).
        if ("Edward".equalsIgnoreCase(getName())) {
            return 3; // Pasta ($18.00 base, $16.20 VIP, 2 prep units)
        }
        return 2; // Burger ($15.00 base, $13.50 VIP, 1 prep unit)
    }
}

