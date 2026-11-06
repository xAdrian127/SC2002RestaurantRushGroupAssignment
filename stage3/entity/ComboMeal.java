package entity;

/**
 * Combo meal menu item (Stage 3).
 * Composed of two existing base menu items (Composite Pattern / Composition).
 * Section 4.3 rules:
 * - Preparation units = sum of its two components.
 * - Price = 90% of the components' combined price, rounded to nearest cent.
 * - Extends MenuItem so it can be ordered polymorphically (LSP).
 */
public class ComboMeal extends MenuItem {
    private final MenuItem firstItem;
    private final MenuItem secondItem;

    public ComboMeal(MenuItem firstItem, MenuItem secondItem) {
        super(
                validateItem(firstItem, "firstItem").getName() + " + "
                        + validateItem(secondItem, "secondItem").getName() + " Combo",
                calculatePrice(firstItem, secondItem),
                firstItem.getPreparationUnits() + secondItem.getPreparationUnits());
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    private static MenuItem validateItem(MenuItem item, String paramName) {
        if (item == null) {
            throw new IllegalArgumentException(paramName + " cannot be null in a ComboMeal");
        }
        if (!item.canBeComboComponent()) {
            throw new IllegalArgumentException(paramName + " must be an individual base MenuItem, not a nested ComboMeal");
        }
        return item;
    }

    private static int calculatePrice(MenuItem firstItem, MenuItem secondItem) {
        int totalPrice = firstItem.getPriceCents() + secondItem.getPriceCents();
        return (totalPrice * 90 + 50) / 100; // Integer half-up rounding to nearest cent
    }

    @Override
    public boolean canBeComboComponent() {
        return false;
    }

    public MenuItem getFirstItem() {
        return firstItem;
    }

    public MenuItem getSecondItem() {
        return secondItem;
    }
}
