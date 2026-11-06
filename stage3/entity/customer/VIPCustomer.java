package entity.customer;

import entity.HappyHour;
import entity.MenuItem;

/**
 * A VIP customer who loses only 5 satisfaction points per eligible turn
 * and receives a 10% discount on their bill.
 * Fulfills Section 4.3 rule: Apply VIP discount first, then Happy Hour discount.
 */
public class VIPCustomer extends Customer {
    public VIPCustomer(String name, int arrivalNumber) {
        super(name, arrivalNumber);
    }

    public VIPCustomer(String name) {
        super(name);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return 5;
    }

    @Override
    public String getCustomerType() {
        return "VIPCustomer";
    }

    @Override
    public int computeOrderBillCents(MenuItem item, boolean isHappyHourActive) {
        // Section 4.3 rule: Apply VIP discount first (10% off), then Happy Hour (20% off),
        // using integer arithmetic rounded to the nearest cent.
        int vipPrice = (item.getPriceCents() * 90 + 50) / 100;
        if (isHappyHourActive) {
            vipPrice = HappyHour.calculateDiscount(vipPrice);
        }
        return vipPrice;
    }

    @Override
    public int chooseMenuChoice() {
        // Documented deterministic VIP preference by arrival sequence (Section 2.2 & 4.3):
        // Early-game VIP (arrival <= 3, e.g. #2) orders Burger (Choice 2: $15.00 base, $13.50 VIP, $10.80 HH);
        // Late-game VIP (arrival > 3, e.g. #5) orders Burger + Pasta Combo (Choice 5: $29.70 base, $26.73 VIP).
        return (getArrivalNumber() <= 3) ? 2 : 5;
    }
}
