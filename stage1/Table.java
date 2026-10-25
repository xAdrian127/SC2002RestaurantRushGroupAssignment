public class Table {
    private int tableNumber;
    private Customer occupiedBy;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.occupiedBy = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Customer getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(Customer customer) {
        this.occupiedBy = customer;
    }

    public boolean isOccupied() {
        return occupiedBy != null;
    }

    public void clearTable() {
        this.occupiedBy = null;
    }
    
    public void seat(Customer customer) {
    	if (!isOccupied() && customer!=null) {
    		occupiedBy = customer;
    	}
    }
}
