package main;

import java.util.Date;
import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;
import flight.City;
import flight.Flight;
import user.Order;
import user.Passenger;
import user.User;

public class MainServer {
	
	private DataManager dataManager;
	private User currentUser;
	private boolean isLogin;
	private boolean isAdmin;
	
	public MainServer() {
		isLogin = false;
		isAdmin = false;
		dataManager = new DataManager();
//		dataManager.init(); // constructor should include init
	}
	
	public boolean Login(String userName, String pass) {
		User tmp;
		for (int i = 0; i < dataManager.admins.size(); i++) {
			tmp = dataManager.admins.get(i);
			if (tmp.getUserName() == userName && tmp.getPassHash().equals(User.hashPass(pass))) {
				isLogin = true;
				isAdmin = true;
				currentUser = tmp;
				return true;
			}
		}
		for (int i = 0; i < dataManager.admins.size(); i++) {
			tmp = dataManager.passengers.get(i);
			if (tmp.getUserName() == userName && tmp.getPassHash().equals(User.hashPass(pass))) {
				isLogin = true;
				currentUser = tmp;
				return true;
			}
		}
		return false;
	}
	
	public boolean isLogin() {
		return isLogin;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	/*
	 *  TODO(all) main function(search, add, ...) at here, each should decide whether isLogin and isAdmin
	 *  if having no permission, throw PermissionDeniedException
	 *  you can see some example(completed) below to know how to throw it
	 *  **make full use of private method searchFlightByID & searchUserByID**
	 */
	private Flight searchFlightByID(int flightID) {
		// TODO(Zhu) searchFlightByID
		return null;
	}
	
	private User searchUserByID(int userID) { // XXX whether should we return User?
		// TODO(Zhu) searchUserByID
		return null;
	}
	
	private City searchCityByID(int CityID) {
		// TODO(Zhu) searchCityByID
		return null;
	}
	
	public boolean createFlight(String flightName, Date startTime, Date arriveTime, int startCityID, int arriveCityID,
			int price, int seatCapacity) throws PermissionDeniedException { // false when erroe cityID
		// TODO(Peng) createFlight
		return false;
	}
	
	public Flight getFlight(int flightID) throws PermissionDeniedException { //give you flight to change freely
		if (isLogin && isAdmin) {
			return searchFlightByID(flightID);			
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public void deleteFlight(int flightID) throws PermissionDeniedException { 
		/* TODO(Peng) deleteFlight
		 * **be sure to delete flight from the city**
		 */
	}
	
	public void queryFlight() {
		// XXX I think this can be changed to a class
	}
	
	public void superQuery() { 
		// XXX I think this can be changed to a class extends queryFlight
	}
	
	public void addAdmin(String userName, String password) throws PermissionDeniedException {
		// TODO(Peng) addAdmin
	}
	
	public boolean deleteUser(int userID) throws PermissionDeniedException {
		/* TODO(Peng) deleteUser
		 * first to test if user is a passenger (using instanceof)
		 * **be sure to remove user from the flight**
		 */
		User u = searchUserByID(userID);
		if (u == null) {
			return false;
		}
		if (u instanceof Passenger) {
			Passenger passenger = (Passenger) u;
			// remove passenger from the flight
		} else {
			
		}
		return false;
	}
	
	public User getCurrentUser() throws PermissionDeniedException { // to update user info
		if (isLogin && isAdmin) {
			return currentUser;
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public void reserveFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException {
		// TODO(Zhu) reserveFlight
		if (isLogin) {
			if (!isAdmin) {
				Passenger passenger = (Passenger) currentUser;
				Flight flight = getFlight(flightID);
				Order order = new Order(passenger, flight, flight.getNumber() + 1);
				passenger.addOrder(order);
				flight.addPassager(passenger);
			}
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public boolean unsubscribeFlight(int flightID) throws PermissionDeniedException { //return false when no flight is found
		// TODO(Peng) unsubscribeFlight
		return false;
	}
	
}
