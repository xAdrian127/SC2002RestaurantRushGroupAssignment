package entity.staff;

/**
 * Abstract base class for all restaurant staff members (Generalization / Inheritance).
 * Encapsulates common turn-based action state and lifecycle operations,
 * adhering to strict encapsulation and the Liskov Substitution Principle (LSP).
 */
public abstract class Staff {
    private boolean actedThisTurn = false;

    /**
     * Checks if the staff member has already taken an action in the current turn.
     *
     * @return true if an action was performed, false otherwise.
     */
    public boolean hasActed() {
        return actedThisTurn;
    }

    /**
     * Marks this staff member as having consumed their turn quota.
     */
    protected void markActed() {
        this.actedThisTurn = true;
    }

    /**
     * Resets the staff member's turn flag at the start of a new turn.
     */
    public void resetTurn() {
        this.actedThisTurn = false;
    }

    /**
     * Marks the staff member as having waited / idled for the turn.
     */
    public void waitTurn() {
        markActed();
    }

    /**
     * Returns the human-readable role name of the staff member.
     *
     * @return Staff role name.
     */
    public abstract String getRoleName();
}
