package entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Restaurant Menu catalog.
 * Defines standard individual menu items and Stage 3 combo meals.
 */
public final class Menu {
    private static final MenuItem SALAD = new MenuItem("Salad", 900, 1);
    private static final MenuItem BURGER = new MenuItem("Burger", 1500, 1);
    private static final MenuItem PASTA = new MenuItem("Pasta", 1800, 2);

    // Stage 3 Combo Meals
    private static final MenuItem COMBO_SALAD_PASTA = new ComboMeal(SALAD, PASTA);   // Choice 4: $24.30, 3 prep units
    private static final MenuItem COMBO_BURGER_PASTA = new ComboMeal(BURGER, PASTA); // Choice 5: $29.70, 3 prep units

    private static final MenuItem[] ITEMS = {
            SALAD,
            BURGER,
            PASTA,
            COMBO_SALAD_PASTA,
            COMBO_BURGER_PASTA
    };

    private static final List<MenuItem> UNMODIFIABLE_ITEMS =
            Collections.unmodifiableList(Arrays.asList(ITEMS));

    private Menu() {
        // Prevent instantiation of catalog class
    }

    public static MenuItem getItem(int choice) {
        if (choice < 1 || choice > ITEMS.length) {
            throw new IllegalArgumentException("Invalid food choice: " + choice);
        }
        return ITEMS[choice - 1];
    }

    public static int getItemCount() {
        return ITEMS.length;
    }

    public static List<MenuItem> getItems() {
        return UNMODIFIABLE_ITEMS;
    }
}
