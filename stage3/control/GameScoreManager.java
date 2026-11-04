package control;

/**
 * Manages game scoring, financial records, customer metrics,
 * and win/loss criteria evaluation (Single Responsibility Principle).
 */
public class GameScoreManager {
    private int totalRevenueCents = 0;
    private int paidCustomerCount = 0;
    private int servedCustomerCount = 0;
    private int unhappyDepartures = 0;
    private double totalSatisfactionOfPaidCustomers = 0.0;

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
        return totalSatisfactionOfPaidCustomers / paidCustomerCount;
    }

    public String getAverageSatisfactionString() {
        if (paidCustomerCount == 0) {
            return "N/A";
        }
        return String.format("%.1f", totalSatisfactionOfPaidCustomers / paidCustomerCount);
    }

    public boolean isTargetCustomersMet() {
        return paidCustomerCount >= 4;
    }

    public boolean isTargetRevenueMet() {
        return getTotalRevenue() >= 45.0;
    }

    public boolean isTargetSatisfactionMet() {
        return getAverageSatisfactionNumeric() >= 60.0;
    }

    public boolean isVictory() {
        return isTargetCustomersMet() && isTargetRevenueMet() && isTargetSatisfactionMet();
    }
}

