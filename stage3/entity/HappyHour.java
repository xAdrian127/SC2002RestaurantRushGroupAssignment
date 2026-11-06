package entity;
public class HappyHour{
    private boolean activated = false;
    private int turnsRemaining = 0;

    //Activate Happy Hour once per game
    public boolean activate(){
        if (activated){
            System.out.println("[DENIED] Happy Hour has already been activated.");
            return false;
        }

        activated = true;
        turnsRemaining = 2;
        System.out.println("[ACTION] Happy Hour activated for 2 turns.");
        return true;
    }

    public boolean isActive(){
        return turnsRemaining>0;
    }

    public int getTurnsRemaining(){
        return turnsRemaining;
    }

    public void endTurn(){
        if (turnsRemaining>0){
            turnsRemaining--;
        }
    }

    public int applyDiscount(int priceCents){
        if (!isActive()){
            return priceCents;
        }
        return (priceCents*80+50)/100;
    }

    public int boostSatisfaction(int satisfaction){
        if (!isActive()){
            return satisfaction;
        }
        return Math.min(100,satisfaction+15);
    }
}