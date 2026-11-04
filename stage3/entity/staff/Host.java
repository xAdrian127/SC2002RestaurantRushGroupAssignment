package entity.staff;

import entity.Table;
import entity.customer.Customer;
import entity.customer.CustomerState;

public class Host{
    private boolean actedThisTurn = false;
    public boolean hasActed(){
        return actedThisTurn;
    }

    public void resetTurn(){
        this.actedThisTurn = false;
    }

    public void waitTurn(){
        this.actedThisTurn = true;
    }

    public boolean seat(Customer customer,Table table){
        if (actedThisTurn){
            System.out.println("[DENIED] Host has already taken an action this turn.");
            return false;
        }
        if (customer==null){
            System.out.println("[DENIED] No customer to seat.");
            return false;
        }
        if (table==null){
            System.out.println("[DENIED] Invalid table.");
            return false;
        }

        if (table.isOccupied()){
            System.out.println("[DENIED] Table"+table.getTableNumber()+"is already occupied.");
            return false;
        }
        table.seat(customer);
        customer.setState(CustomerState.SEATED);
        this.actedThisTurn = true;
        System.out.println("[ACTION] Host seated"+customer.getName()+"("+customer.getClass().getSimpleName()+") at Table "+table.getTableNumber());
        return true;
    }

}