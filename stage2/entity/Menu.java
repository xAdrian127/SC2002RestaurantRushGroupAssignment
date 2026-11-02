package entity;

public class Menu {
    private static final MenuItem[] Items = {
            new MenuItem("Salad", 900, 1),
            new MenuItem("Burger", 1500, 1),
            new MenuItem("Pasta", 1800, 2)
    };

    public static MenuItem Choice(int choice) {
        if (choice < 1 || choice > Items.length) {
            throw new IllegalArgumentException("Invalid food choice");
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

