package entity;

/**
 * Happy Hour dynamic pricing policy (Stage 3).
 * Activated once per game at turn start; lasts for that turn and the next (2 turns total).
 * Section 4.3 requirements:
 * - Orders taken while it is active get 20% off their bill (locked when order is taken).
 * - During each active turn, customers still waiting for a table or food recover 15 satisfaction at turn start.
 * - Displays its remaining duration.
 */
public class HappyHour {
    private boolean activated = false;
    private int turnsRemaining = 0;

    /**
     * Activates Happy Hour once per game.
     *
     * @return true if activation succeeded, false if already activated previously.
     */
    public boolean activate() {
        if (activated) {
            return false;
        }
        activated = true;
        turnsRemaining = 2; // Active for this turn and the next turn
        return true;
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean isActive() {
        return turnsRemaining > 0;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Decrements the remaining duration at the end of each turn.
     */
    public void endTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    /**
     * Pure calculation: applies the 20% Happy Hour discount (retains 80%)
     * rounded to the nearest cent using integer arithmetic.
     * Section 4.3 rule: (price * 80 + 50) / 100.
     *
     * @param priceCents Price in cents before Happy Hour discount.
     * @return Discounted price in cents.
     */
    public static int calculateDiscount(int priceCents) {
        return (priceCents * 80 + 50) / 100;
    }
}
