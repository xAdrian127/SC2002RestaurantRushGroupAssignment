package entity.customer;

import entity.HappyHour;
import entity.MenuItem;

/**
 * A VIP customer who loses only 5 satisfaction points per eligible turn
 * and receives a 10% discount on their bill.
 * Fulfills Section 4.3 rule: Apply VIP discount first, then Happy Hour discount.
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
    public int computeOrderBillCents(MenuItem item, boolean isHappyHourActive) {
        // Section 4.3 rule: Apply VIP discount first (10% off), then Happy Hour (20% off)
        int vipPrice = (int) Math.round(item.getPriceCents() * 0.90);
        if (isHappyHourActive) {
            vipPrice = HappyHour.calculateDiscount(vipPrice);
        }
        return vipPrice;
    }

    @Override
    public int chooseMenuChoice() {
        // Documented VIP order preference:
        // Bob prefers Burger (Choice 2: $15.00 base, $13.50 VIP, or $10.80 if Happy Hour)
        // Edward prefers a ComboMeal (Choice 5: Burger + Pasta Combo, base $29.70, $26.73 VIP, 3 prep units)
        if ("Edward".equalsIgnoreCase(getName())) {
            return 5; // Burger + Pasta Combo (Section 4.3 requirement)
        }
        return 2; // Burger
    }
}
