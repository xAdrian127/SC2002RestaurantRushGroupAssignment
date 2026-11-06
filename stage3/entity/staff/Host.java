package entity.staff;

/**
 * Host staff role (Stage 3).
 * Seats at most one queued customer per turn in addition to the Waiter's task.
 * Extends SeatingStaff to inherit shared seating domain behavior (DRY / LSP).
 */
public class Host extends SeatingStaff {

    @Override
    public String getRoleName() {
        return "Host";
    }
}
