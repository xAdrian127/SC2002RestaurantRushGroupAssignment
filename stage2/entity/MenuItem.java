public class MenuItem {
    private final String name;
    private final int priceCents;
    private final int preparationUnits;

    public MenuItem(String name, int priceCents, int preparationUnits) {
        this.name = name;
        this.priceCents = priceCents;
        this.preparationUnits = preparationUnits;
    }

    public MenuItem(MenuItem menuItem) {
        this(menuItem.getName(), menuItem.getPriceCents(), menuItem.getPreparationUnits());
    }

    // Getters
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
