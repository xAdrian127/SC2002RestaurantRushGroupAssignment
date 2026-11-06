package entity;

/**
 * Immutable base menu item with name, price in integer cents, and kitchen preparation units.
 */
public class MenuItem {
    private final String name;
    private final int priceCents;
    private final int preparationUnits;

    public MenuItem(String name, int priceCents, int preparationUnits) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("MenuItem name cannot be null or blank");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("MenuItem priceCents cannot be negative");
        }
        if (preparationUnits <= 0) {
            throw new IllegalArgumentException("MenuItem preparationUnits must be positive");
        }
        this.name = name;
        this.priceCents = priceCents;
        this.preparationUnits = preparationUnits;
    }

    public MenuItem(MenuItem menuItem) {
        this(menuItem.getName(), menuItem.getPriceCents(), menuItem.getPreparationUnits());
    }

    /**
     * Indicates whether this menu item can be used as a component inside a ComboMeal.
     * Base individual items return true; composite items override to return false.
     */
    public boolean canBeComboComponent() {
        return true;
    }

    public String getName() {
        return name;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getPreparationUnits() {
        return preparationUnits;
    }
}
