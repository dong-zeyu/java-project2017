package user;

import java.util.ArrayList;

public class Passenger extends User {
	
	String identityID;
	ArrayList<Order> orderList;

	public Passenger(String identityID, String realName, String password) {
		this.identityID = identityID;
		this.userName = realName;
		this.userID = User.ID;
		User.ID++;
		// TODO hash pass
	}
	
	public void addOrder(Order order) {
		// TODO add Order
	}
}
