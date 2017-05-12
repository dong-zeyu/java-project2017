package main;

import java.util.Date;

import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;
import flight.City;
import flight.Flight;
import flight.FlightStatus;
import user.Admin;
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

	public void stop() {
		dataManager.stop();
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
	
	public boolean createFlight(String flightName, Date startTime, Date arriveTime, int startCityID, int arriveCityID,
			int price, int seatCapacity) throws PermissionDeniedException { // false when error cityID
		// DONE(Peng) creatFlight
		 if (isLogin&&isAdmin){
			try {
				dataManager.flights.add(new Flight(flightName,startTime, arriveTime,dataManager.getCityByID(startCityID),dataManager.getCityByID(arriveCityID),
					price,seatCapacity));
				return true;
			} catch (NullPointerException e) {
				return false;
			}
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public Flight getFlight(int flightID) throws PermissionDeniedException { //give you flight to change freely
		if (isLogin && isAdmin) {
			return dataManager.getFlightByID(flightID);			
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public City getCity(int cityID) throws PermissionDeniedException { //give you city to change freely
		if (isLogin && isAdmin) {
			return dataManager.getCityByID(cityID);			
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public boolean deleteFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException	 {
		/* DONE(Peng) deleteFlight
		 * **be sure to delete flight from the city**
		 * FIXME (Peng) be careful about the comment above!
		 */
		if (isLogin && isAdmin) {
			Flight f =dataManager.getFlightByID(flightID);
			if (f.getFlightStatus()==FlightStatus.UNPUBLISHED){
				dataManager.flights.remove(f);
			} else {
				throw new StatusUnavailableException();
			}
		} else {
			throw new PermissionDeniedException();
		}
		return false; 
	}
	
	public void queryFlight() {
		// XXX I think this can be changed to a class
	}
	
	public void superQuery() { 
		// XXX I think this can be changed to a class extends queryFlight
	}
	
	public void addPassenger(String username, String idNumber, String password){
		dataManager.users.add(new Passenger(idNumber, username, password));
	}
	
	public void addAdmin(String userName, String password) throws PermissionDeniedException {
		// DONE(Peng) addAdmin
		if (isLogin){
            if (isAdmin){    	
            	dataManager.users.add(new Admin(userName, password));
            } else
            	throw new PermissionDeniedException();
		}
	}
	
	public void addCity(String cityName) throws PermissionDeniedException{
		//DONE(Zhu) addCity
		if(isAdmin){
			dataManager.cities.add(new City(cityName));
		}else{
			throw new PermissionDeniedException();
		}
	}
	
	public boolean deleteUser(int userID) throws PermissionDeniedException, StatusUnavailableException {
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
			for (Order order : passenger.getOrderList()) {
				order.getFlight().removePassenger(passenger);
			}
		} else {
			dataManager.users.remove(u);
		}
		return true;
	}
	
	public User getCurrentUser() throws PermissionDeniedException { // to update user info
		if (isLogin && isAdmin) {
			return currentUser;
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public boolean reserveFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException {
		// DONE(Zhu) reserveFlight
		if (isLogin && !isAdmin) {
			Flight flight = dataManager.getFlightByID(flightID);
			if (flight != null) {
				flight.addPassager((Passenger) currentUser);
				return true;
			}
		} else {
			throw new PermissionDeniedException();
		}
		return false;
	}
	
	public boolean unsubscribeFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException { //return false when no flight is found
		//DONE(Peng) unsubscribeFlight
		if (isLogin) {
			if (!isAdmin) {
				Passenger passenger = (Passenger) getCurrentUser();
				Flight flight = dataManager.getFlightByID(flightID);
				if (flight != null) {
					flight.removePassenger(passenger);
					return true;
				}
			} else {
				throw new StatusUnavailableException("Only passengers can unsubscribe flight");
			}
		} else {
			throw new PermissionDeniedException();
		}
		return false;
	}

	public boolean deleteCity(int cityID) throws PermissionDeniedException, StatusUnavailableException {
		if (isAdmin) {
			City city = dataManager.getCityByID(cityID);
			if (city != null) {
				if (city.getFlightsIn().size() == 0 && city.getFlightsOut().size() == 0) {
					dataManager.cities.remove(city);					
				} else {
					throw new StatusUnavailableException("Cannot delete city that have fight in and out");
				}
				return true;
			} 
		} else {
			throw new PermissionDeniedException();
		}
		return false;
	}
	
	/*
	 * return a string with this format:
	 * ID	Name	....
	 * 1	A001	....
	 * .	.
	 * .	.
	 * tips: see <Flight>.toString()
	 */
	public String displayCity() {
		// TODO(Zhu)
		return null;
	}

	public String displayFlight() {
		// TODO(Zhu) 
		return null;
	}

	public String dispalyUser() {
		// TODO(Peng)
		return null;
	}
	
	public String displayFlight(int i) {
		// DONE(Dong)
		StringBuilder resultbuilder = new StringBuilder();
		Flight flight = dataManager.getFlightByID(i);
		resultbuilder.append(flight.toString() + "\n");
		if (isAdmin) {
			resultbuilder.append("\tPasengers:\n");
			for (Passenger passenger : flight.getPassagers()) {
				resultbuilder.append("\t\t" + passenger.toString() + "\n");
			}
		}
		return resultbuilder.toString();
	}
	//------------boundary-----------------

	public String search(int cityFromId, int cityToId, Date date1, Date date2) {
		// TODO(Dong) The most difficult one
		return null;
	}
	
}
