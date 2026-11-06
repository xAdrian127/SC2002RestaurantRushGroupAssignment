package entity;

/**
 * Restaurant Menu catalog.
 * Defines standard individual menu items and Stage 3 combo meals.
 */
public class Menu {
    private static final MenuItem SALAD = new MenuItem("Salad", 900, 1);
    private static final MenuItem BURGER = new MenuItem("Burger", 1500, 1);
    private static final MenuItem PASTA = new MenuItem("Pasta", 1800, 2);

    // Stage 3 Combo Meals
    private static final MenuItem COMBO_SALAD_PASTA = new ComboMeal(SALAD, PASTA);   // Choice 4: $24.30, 3 prep units
    private static final MenuItem COMBO_BURGER_PASTA = new ComboMeal(BURGER, PASTA); // Choice 5: $29.70, 3 prep units

    private static final MenuItem[] Items = {
            SALAD,
            BURGER,
            PASTA,
            COMBO_SALAD_PASTA,
            COMBO_BURGER_PASTA
    };

    public static MenuItem Choice(int choice) {
        if (choice < 1 || choice > Items.length) {
            throw new IllegalArgumentException("Invalid food choice: " + choice);
        }
        return Items[choice - 1];
    }

    public static int getItemCount() {
        return Items.length;
    }

    public static MenuItem[] getItems() {
        return Items;
    }
}
