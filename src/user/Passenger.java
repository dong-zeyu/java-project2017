package user;

import java.util.ArrayList;

public class Passenger extends User {
	
	String identityID; // XXX this should not be changed?
	ArrayList<Order> orderList;

	public Passenger(String identityID, String realName, String password) {
		this.identityID = identityID;
		this.userName = realName;
		this.userID = User.ID;
		User.ID++;
		passHash = hashPass(password);
	}
	
	public void addOrder(Order order) {
		orderList.add(order);
	}
	
}
