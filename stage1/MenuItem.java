
public class MenuItem {
	private String name;
	private int priceCents;
	private int preparationUnits;
	
	public MenuItem(String name, int priceCents, int preparationUnits) {
		this.name = name;
		this.priceCents = priceCents;
		this.preparationUnits = preparationUnits;
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
