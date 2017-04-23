package user;

import java.util.ArrayList;

public class Passenger extends User {
	
	private String identityID; // XXX this should not be changed?
	private ArrayList<Order> orderList;

	public Passenger(String identityID, String realName, String password) {
		orderList = new ArrayList<>();
		this.identityID = identityID;
		this.userName = realName;
		this.userID = User.ID;
		User.ID++;
		passHash = hashPass(password);
	}
	
	public String getIdentityID() {
		return identityID;
	}
	
	public void addOrder(Order order) {
		orderList.add(order);
	}
	
	public ArrayList<Order> getOrderList() {
		return (ArrayList<Order>) orderList.clone();
	}
	
}
