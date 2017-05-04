package user;

import java.util.ArrayList;

public class Passenger extends User {
	
	private String identityID; // XXX this should not be changed?
	private ArrayList<Order> orderList;
	
	public Passenger(String identityID, String realName, String password) {
		this(identityID, realName, password, false);
	}

	public Passenger(String identityID, String realName, String password, boolean isPassHash) {
		orderList = new ArrayList<>();
		this.identityID = identityID;
		this.userName = realName;
		this.userID = User.ID;
		User.ID++;
		if (isPassHash) {
			passHash = password;
		} else {
			passHash = hashPass(password);
		}
	}
	
	@Override
	public int hashCode() {
        int hashCode = 1;
        hashCode = 31*hashCode + userName.hashCode();
        hashCode = 31*hashCode + passHash.hashCode();
        hashCode = 31*hashCode + identityID.hashCode();
        hashCode = 31*hashCode + orderList.hashCode();
        return hashCode;
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
