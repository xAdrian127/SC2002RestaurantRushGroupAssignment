
public class Waiter {
	
	public void seat(Customer customer, Table table) {
		table.seat(customer);
	}
	
	public Order takeOrder(Customer customer, MenuItem food) {
		Order order = new Order(food.getName(), food.getPriceCents()/100.0, food.getPreparationUnits());
		customer.setOrder(order);
		return order;
	}
	
	public void serve(Order order, int turn) {
		order.serve(turn);
	}

}
