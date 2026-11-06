package entity;
public class ComboMeal extends MenuItem{
    private final MenuItem firstItem;
    private final MenuItem secondItem;

    public ComboMeal(MenuItem firstItem, MenuItem secondItem) {
        super(
            firstItem.getName() + " + " + secondItem.getName() + " Combo",
            calculatePrice(firstItem, secondItem),
            firstItem.getPreparationUnits() + secondItem.getPreparationUnits()
        );

        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    private static int calculatePrice(MenuItem firstItem, MenuItem secondItem){
        int totalPrice = firstItem.getPriceCents()+secondItem.getPriceCents();
        return (totalPrice*90+50)/100; //+50/100 to correctly round to nearest cent
    }
    public MenuItem getFirstItem() {
        return firstItem;
    }

    public MenuItem getSecondItem() {
        return secondItem;
    }
}