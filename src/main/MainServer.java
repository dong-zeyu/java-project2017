package main;

import java.util.Date;
import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;
import flight.City;
import flight.Flight;
import user.Admin;
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
		for (User user : dataManager.users) {
			if (user.getUserName().equals(userName) && user.getPassHash().equals(User.hashPass(pass))) {
				isLogin = true;
				if (user instanceof Admin) {					
					isAdmin = true;
				} else {
					isAdmin = false;
				}
				currentUser = user;
				return true;
			} else {
				isLogin = false;
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
	
	public void createFlight(String flightName, Date startTime, Date arriveTime, int startCityID, int arriveCityID,
			int price, int seatCapacity) throws PermissionDeniedException { // false when erroe cityID
		// TODO(Peng) createFlight
	}
	
	public Flight getFlight(int flightID) throws PermissionDeniedException { //give you flight to change freely
		if (isLogin && isAdmin) {
			return dataManager.getFlightByID(flightID);			
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public boolean deleteFlight(int flightID) throws PermissionDeniedException {
		/* TODO(Peng) deleteFlight
		 * **be sure to delete flight from the city**
		 */
		return false; 
	}
	
	public void queryFlight() {
		// XXX I think this can be changed to a class
	}
	
	public void superQuery() { 
		// XXX I think this can be changed to a class extends queryFlight
	}
	
	public void addUser(String username,String IDNUMBER,String password){
		
	}
	
	public void addAdmin(String userName, String password) throws PermissionDeniedException {
		// TODO(Peng) addAdmin
		
	}
	
	public void addCity(String cityName) throws PermissionDeniedException{
		//DONE(Zhu) addCity
		if(isAdmin){
			dataManager.cities.add(new City(cityName));
		}else{
			throw new PermissionDeniedException();
		}
	}
	
	public boolean deleteUser(int userID) throws PermissionDeniedException {
		/* TODO(Peng) deleteUser
		 * first to test if user is a passenger (using instanceof)
		 * **be sure to remove user from the flight**
		 */
		User u = dataManager.getUserByID(userID);
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
		// DONE(Zhu) reserveFlight
		if (isLogin) {
			if (!isAdmin) {
				Flight flight = dataManager.getFlightByID(flightID);
				flight.addPassager((Passenger) currentUser);
			}
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public boolean unsubscribeFlight(int flightID) throws PermissionDeniedException { //return false when no flight is found
		// TODO(Peng) unsubscribeFlight
		Flight flight=getFlight(flightID);
		if (flight!=null){ // FIXME(Peng) what's this mean?
			throw new PermissionDeniedException();
		}
		return false;	
		
		
	}

	public void stop() throws Throwable {
		dataManager.finalize();
	}
	
}
