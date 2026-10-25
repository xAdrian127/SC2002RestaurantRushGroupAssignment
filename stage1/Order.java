public class Order {
    private String foodName;
    private double price;
    private int prepUnitsLeft;
    private boolean isReady;
    private boolean isServed;
    private int turnServed = -1;
    private boolean readyToPay = false;
    private int turnReadyToPay = -1;

    public Order(String foodName, double price, int prepUnits) {
        this.foodName = foodName;
        this.price = price;
        this.prepUnitsLeft = prepUnits;
        this.isReady = (prepUnits == 0);
        this.isServed = false;
        this.turnServed = -1;
        this.readyToPay = false;
        this.turnReadyToPay = -1;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getPrice() {
        return price;
    }

    public int getPrepUnitsLeft() {
        return prepUnitsLeft;
    }

    public void setPrepUnitsLeft(int prepUnitsLeft) {
        this.prepUnitsLeft = prepUnitsLeft;
        if (this.prepUnitsLeft <= 0) {
            this.isReady = true;
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        this.isReady = ready;
    }

    public boolean isServed() {
        return isServed;
    }

    public void setServed(boolean served) {
        this.isServed = served;
    }

    public int getTurnServed() {
        return turnServed;
    }

    public void setTurnServed(int turnServed) {
        this.turnServed = turnServed;
    }

    public boolean isReadyToPay() {
        return readyToPay;
    }

    public void setReadyToPay(boolean readyToPay) {
        this.readyToPay = readyToPay;
    }

    public int getTurnReadyToPay() {
        return turnReadyToPay;
    }

    public void setTurnReadyToPay(int turnReadyToPay) {
        this.turnReadyToPay = turnReadyToPay;
    }
    
    public void advancePreparation() {
    	if (!isReady && prepUnitsLeft>0) {
    		prepUnitsLeft--;
    		if (prepUnitsLeft==0) {
    			isReady = true;
    		}
    	}
    }
    
    public void serve(int turn) {
    	if(isReady && !isServed) {
    		isServed = true;
    		turnServed = turn;
    	}
    }
    
    
}
