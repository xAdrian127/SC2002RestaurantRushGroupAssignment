package entity.staff;

import entity.Order;
import entity.Table;

/**
 * Chef staff role.
 * Advances one preparation unit per turn for a chosen pending order.
 * Pure domain entity with zero console I/O (BCE / SRP).
 */
public class Chef extends Staff {

    @Override
    public String getRoleName() {
        return "Chef";
    }

    public boolean cook(Table table) {
        if (hasActed() || table == null || !table.hasCookableOrder()) {
            return false;
        }
        Order order = table.getActiveOrder();
        order.advancePreparation();
        markActed();
        return true;
    }
}
