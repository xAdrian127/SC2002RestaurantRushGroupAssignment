public class PaymentLedger {
    public static final int TARGET_PAID_CUSTOMERS = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final int TARGET_AVERAGE_SATISFACTION = 60;

    private int paidCount = 0;
    private int revenueCents = 0;
    private int satisfactionSum = 0;

    public void recordPayment(Customer customer, Order order) {
        revenueCents += (int) Math.round(order.getPrice() * 100);
        satisfactionSum += customer.getSatisfaction();
        paidCount++;
    }

    public int getPaidCount() {
        return paidCount;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public String formatRevenue() {
        return String.format("$%d.%02d", revenueCents / 100, revenueCents % 100);
    }

    public double getAverageSatisfaction() {
        return paidCount == 0 ? -1 : (double) satisfactionSum / paidCount;
    }

    public String formatAverageSatisfaction() {
        return paidCount == 0 ? "N/A" : String.format("%.1f", getAverageSatisfaction());
    }

    public boolean paidTargetMet() {
        return paidCount >= TARGET_PAID_CUSTOMERS;
    }

    public boolean revenueTargetMet() {
        return revenueCents >= TARGET_REVENUE_CENTS;
    }

    public boolean satisfactionTargetMet() {
        return paidCount > 0 && getAverageSatisfaction() >= TARGET_AVERAGE_SATISFACTION;
    }

    public boolean isVictory() {
        return paidTargetMet() && revenueTargetMet() && satisfactionTargetMet();
    }
}
