package main;

import java.util.ArrayList;
import java.util.Date;
import data.Admin;
import data.City;
import data.DataManager;
import data.Flight;
import data.FlightDaemon;
import data.FlightStatus;
import data.Order;
import data.Passenger;
import data.User;
import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;

public class MainServer {
	
	private DataManager dataManager;
	private User currentUser;
	private boolean isLogin;
	private boolean isAdmin;
	
	public MainServer() {
		isLogin = false;
		isAdmin = false;
		dataManager = new DataManager();
	}

	public void stop() {
		dataManager.stop();
	}
	
	public boolean login(String userName, String pass) {
		isLogin = false;
		isAdmin = false;
		currentUser = null;
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
	
	private void checkPermission(boolean requireAdmin) throws PermissionDeniedException {
		// Require
		if (!isLogin) {
			throw new PermissionDeniedException("please login");
		} else if (requireAdmin && !isAdmin) {
			throw new PermissionDeniedException("you are not administrator");
		}
	}
	
	/*
	 *  DONE(all) main function(search, add, ...) at here, each should decide whether isLogin and isAdmin
	 *  if having no permission, throw PermissionDeniedException
	 *  you can see some example(completed) below to know how to throw it
	 *  **make full use of private method searchFlightByID & searchUserByID**
	 */
	public boolean createFlightDaemon(String flightName, Date startTime, Date arriveTime, int period, int startCityID, int arriveCityID,
			int price, int seatCapacity, int distence) throws PermissionDeniedException { // false when error cityID
		// DONE(Peng) creatFlight
		checkPermission(true);
		try {
			dataManager.flightDaemons.add(new FlightDaemon(flightName,startTime, arriveTime, period*24*3600*1000,
					dataManager.getCityByID(startCityID),dataManager.getCityByID(arriveCityID),
					price,seatCapacity, distence));
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public Flight getFlight(int flightID) throws PermissionDeniedException { //give you flight to change freely
		checkPermission(true);
		return dataManager.getFlightByID(flightID);
	}
	
	
	public FlightDaemon getDaemon(int flightID) throws PermissionDeniedException {
		checkPermission(true);
		return dataManager.getFlightDaemonByID(flightID);
	}
	
	public City getCity(int cityID) throws PermissionDeniedException { //give you city to change freely
		checkPermission(true);
		return dataManager.getCityByID(cityID);	
	}
	
	public boolean deleteFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException	 {
		// DONE(Peng) deleteFlight
		checkPermission(true);
		Flight f =dataManager.getFlightByID(flightID);
		
		if (f != null) {
			if (f.getFlightStatus() == FlightStatus.UNPUBLISHED) {
				dataManager.flights.remove(f);
				f.delete();
				return true;
			} else {
				throw new StatusUnavailableException(f.getFlightStatus());
			}
		}
		return false; 
	}
	
	public boolean deleteFlightDaemon(int daemonID) throws PermissionDeniedException {
		checkPermission(true);
		FlightDaemon daemon = dataManager.getFlightDaemonByID(daemonID);
		
		if (daemon != null) {
			daemon.removeFlight();
			dataManager.flights.removeAll(daemon.getChildren());
		}
		return false;
	}
	
	public void addPassenger(String username, String idNumber, String password){
		dataManager.users.add(new Passenger(idNumber, username, password));
	}
	
	public void addAdmin(String userName, String password) throws PermissionDeniedException {
		// DONE(Peng) addAdmin
		checkPermission(true);
		dataManager.users.add(new Admin(userName, password));
	}
	
	public void addCity(String cityName) throws PermissionDeniedException{
		//DONE(Zhu) addCity
		checkPermission(true);
		dataManager.cities.add(new City(cityName));
	}
	
	public boolean deleteUser(int userID) throws PermissionDeniedException {
		/* DONE(Peng) deleteUser
		 * first to test if user is a passenger (using instanceof)
		 * **be sure to remove user from the flight**
		 */
		checkPermission(true);
		User u = dataManager.getUserByID(userID);
		if (u == null) {
			return false;
		}
		if (u instanceof Passenger) {
			Passenger passenger = (Passenger) u;
			for (Order order : passenger.getOrderList()) {
				order.remove();
			}
			dataManager.users.remove(u);
		} else {
			dataManager.users.remove(u);
		}
		return true;
	}
	
	public User getCurrentUser() throws PermissionDeniedException { // to update user info
		checkPermission(false);
		return currentUser;
	}
	
	public boolean reserveFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException {
		// DONE(Zhu) reserveFlight
		checkPermission(false);
		if (isAdmin) {
			throw new PermissionDeniedException("adminstrator cannot reserve flight");
		}
		Flight flight = dataManager.getFlightByID(flightID);
		if (flight != null) {
			((Passenger) currentUser).reserveFlight(flight);
			return true;
		}
		return false;
	}
	
	public boolean unsubscribeFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException { //return false when no flight is found
		//DONE(Peng) unsubscribeFlight
		checkPermission(false);
		if (!isAdmin) {
			Passenger passenger = (Passenger) getCurrentUser();
			Flight flight = dataManager.getFlightByID(flightID);
			if (flight != null) {
				passenger.unsubscribeFlight(flight);
				return true;
			}
		} else {
			throw new StatusUnavailableException("Only passengers can unsubscribe flight");
		}
		return false;
	}
	
	public void pay(int index) throws PermissionDeniedException, StatusUnavailableException {
		// DONE(Peng) pay an order (index is the index of the order in ArrayList<Order>)
		checkPermission(false);
		   if (!isAdmin){
			   Passenger passenger = (Passenger) currentUser;
			   passenger.getOrderList().get(index).pay();
			
		   } else {
			   throw new PermissionDeniedException("sorry you are not the user");
		}
		
	}
	
	public void cancel(int index) throws PermissionDeniedException, StatusUnavailableException{
		// DONE(Peng) cancel an order (index is the index of the order in ArrayList<Order>)
		checkPermission(false);
		if (!isAdmin) {
			Passenger passenger = (Passenger) currentUser;
			passenger.getOrderList().get(index).cancle();
			
		} else {
			throw new PermissionDeniedException("sorry you are not the user");
		}
	}
	
	public boolean deleteCity(int cityID) throws PermissionDeniedException, StatusUnavailableException {
		checkPermission(true);
		City city = dataManager.getCityByID(cityID);
		if (city != null) {
			if (city.getFlightsIn().size() == 0 && city.getFlightsOut().size() == 0) {
				dataManager.cities.remove(city);					
			} else {
				throw new StatusUnavailableException("Cannot delete city that have fight in and out");
			}
			return true;
		}
		return false;
	}
	
	/*
	 * return a string with this format:
	 * ID	Name	....
	 * 1	A001	....
	 * .	.		.
	 * .	.		 .
	 * tips: see <Flight>.toString() and <Order>.toString()
	 */
	public String displayCity() {
		// TODO(Zhu) displayCity
		return null;
	}

	public String displayFlight() {
		// TODO(Zhu) displayFlight
		return null;
	}

	public String displayDaemon() {
		// TODO(Zhu) displayDaemon
		return null;
	}

	public String dispalyUser() throws PermissionDeniedException {
		//DONE(Peng)
		checkPermission(true);
		StringBuilder resultbuilder = new StringBuilder();
		resultbuilder.append("userID\tuserName\tisAdmin\n");
		for (User user : dataManager.users) {
			resultbuilder.append(String.valueOf(user.getID()) + "\t" + user.toString() + "\t\t" + String.valueOf(user instanceof Admin) + "\n");		
		}
		return resultbuilder.toString();
	}
	
	public String displayFlight(int flightID) {
		// DONE(Dong)
		StringBuilder resultbuilder = new StringBuilder();
		Flight flight = dataManager.getFlightByID(flightID);
		resultbuilder.append(flight.toString() + "\n");
		if (isAdmin) {
			resultbuilder.append("\tPasengers:\n");
			for (Passenger passenger : flight.passagers().keySet()) {
				resultbuilder.append("\t\t" + passenger.toString() + "\n");
			}
		}
		return resultbuilder.toString();
	}
	/*
	 * when it comes to display a object with specific id, you need provide more specific information(see above)
	 */
	public String displayCity(int CityID) {
		// DONE(Peng) print flightIn and flightOut as well
		if (isLogin){
			System.out.println("the name of the City is : "+ dataManager.getCityByID(CityID).getCityName());
			System.out.println("Flights to this City are : "+ dataManager.getCityByID(CityID).getFlightsIn());
			System.out.println("Flights living this City are : "+ dataManager.getCityByID(CityID).getFlightsOut());
		}
		else {
			System.out.println("Sorry your are not logged in");
		}
		return null;
	}
	
	public String dispalyUser(int UserID) throws PermissionDeniedException {
		// TODO(Zhu) print User order(if it is passenger) as well
		return null;
	}
	
	public String displayOrder() throws PermissionDeniedException {
		// TODO(Zhu) print all the order in order (if isLogin)
		return null;
		
	}

	//------------boundary-----------------

	public String search(int cityFromId, int cityToId, Date date1, Date date2) {
		// DONE(Dong) give up...
		StringBuilder builder = new StringBuilder();
		City from = dataManager.getCityByID(cityFromId);
		City to = dataManager.getCityByID(cityToId);
		long bdate;
		long edate;
		if (date1 == null) {
			bdate = 0;
		} else {
			bdate = date1.getTime();
		}
		if (date2 == null) {
			edate = Long.MAX_VALUE;
		} else {
			edate = date2.getTime();
		}
		ArrayList<Flight> flights = new ArrayList<>();
		for (Flight flight : dataManager.flights) {
			if (flight.getStartTime().getTime() >= bdate 
					&& flight.getStartTime().getTime() <= edate 
					&& flight.getFlightStatus() != FlightStatus.TERMINATE) {
				flights.add(flight);
			}
		}
		builder.append("ID\tName\tStartCity\tArriveCity\tStartTime\t\t\tArriveTime\t\t\tPrice\tSeatRemain\n");
		for (Flight flight : flights) {
			if (flight.getStartCity().equals(from) && flight.getArriveCity().equals(to)) {
				builder.append(flight.toString() + "\n");
			}
		}
		return builder.toString();
	}
	
	public String search(String flightName) {
		StringBuilder builder = new StringBuilder();
		builder.append("ID\tName\tStartCity\tArriveCity\tStartTime\t\t\tArriveTime\t\t\tPrice\tSeatRemain\n");
		for (Flight flight : dataManager.flights) {
			if (flight.getFlightStatus() != FlightStatus.TERMINATE && flight.getFlightName().toLowerCase().contains(flightName.toLowerCase())) {
				builder.append(flight.toString() + "\n");
			}
		}
		return builder.toString();
	}

}
