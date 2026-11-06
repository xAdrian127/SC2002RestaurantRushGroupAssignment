package control;

/**
 * Manages game scoring, financial records in integer cents, customer metrics,
 * and win/loss criteria evaluation (Single Responsibility Principle & Information Expert).
 */
public class GameScoreManager {
    public static final int TARGET_PAID_CUSTOMERS = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final double TARGET_AVERAGE_SATISFACTION = 60.0;

    private int totalRevenueCents = 0;
    private int paidCustomerCount = 0;
    private int servedCustomerCount = 0;
    private int unhappyDepartures = 0;
    private int totalSatisfactionOfPaidCustomers = 0;

    public void recordPayment(int cents, int satisfaction) {
        this.totalRevenueCents += cents;
        this.paidCustomerCount++;
        this.totalSatisfactionOfPaidCustomers += satisfaction;
    }

    public void recordServed() {
        this.servedCustomerCount++;
    }

    public void recordDeparture() {
        this.unhappyDepartures++;
    }

    public int getTotalRevenueCents() {
        return totalRevenueCents;
    }

    public double getTotalRevenue() {
        return totalRevenueCents / 100.0;
    }

    public int getPaidCustomerCount() {
        return paidCustomerCount;
    }

    public int getServedCustomerCount() {
        return servedCustomerCount;
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }

    public double getAverageSatisfactionNumeric() {
        if (paidCustomerCount == 0) {
            return 0.0;
        }
        return (double) totalSatisfactionOfPaidCustomers / paidCustomerCount;
    }

    public String getAverageSatisfactionString() {
        if (paidCustomerCount == 0) {
            return "N/A";
        }
        return String.format("%.1f", getAverageSatisfactionNumeric());
    }

    public boolean isTargetCustomersMet() {
        return paidCustomerCount >= TARGET_PAID_CUSTOMERS;
    }

    public boolean isTargetRevenueMet() {
        return totalRevenueCents >= TARGET_REVENUE_CENTS;
    }

    public boolean isTargetSatisfactionMet() {
        return getAverageSatisfactionNumeric() >= TARGET_AVERAGE_SATISFACTION;
    }

    public boolean isVictory() {
        return isTargetCustomersMet() && isTargetRevenueMet() && isTargetSatisfactionMet();
    }
}
