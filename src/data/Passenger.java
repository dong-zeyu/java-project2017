package data;

import java.util.ArrayList;

import exceptions.StatusUnavailableException;

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
	
	public void reserveFlight(Flight flight) throws StatusUnavailableException {
		for (Order order : orderList) {
			if (order.getFlight() == flight) {
				throw new StatusUnavailableException("has already reserved");
			}
		}
		 Order order = new Order(this, flight);
		 this.addOrder(order);
	}
	
	public boolean unsubscribeFlight(Flight flight) throws StatusUnavailableException {
		for (Order order : orderList) {
			if (order.getFlight() == flight) {
				order.cancle();
				return true;
			}
		}
		return false;
	}
	
	public void addOrder(Order order) {
		orderList.add(order);
	}
	
	protected ArrayList<Order> getOrderList() {
		return orderList;
	}
	
	public ArrayList<Order> orderList() {
		return (ArrayList<Order>) orderList.clone();
	}
	
	public boolean removeOrder(Order order){
		return orderList.remove(order);
	}

}
