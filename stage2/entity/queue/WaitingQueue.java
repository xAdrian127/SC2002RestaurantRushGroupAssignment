import java.util.ArrayList;
public class WaitingQueue {
	private ArrayList<Customer> customers = new ArrayList<>();
	public void add(Customer customer) {
		if (!customers.contains(customer)) {
			customers.add(customer);
		}
	}
	
	public Customer removeFirst() {
		if (customers.isEmpty()) {
			return null;
		}
		return customers.remove(0);
	}
	
	public boolean isEmpty() {
		return customers.isEmpty();
	}
	
	public ArrayList<Customer> getCustomers(){
		return new ArrayList<>(customers);
	}

}
