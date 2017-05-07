package main;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.rmi.server.ServerCloneException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;
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
	
	public boolean Login(String userName, String pass) {
		User tmp;
		for (int i = 0; i < dataManager.users.size(); i++) {
			tmp = dataManager.users.get(i);
			if (tmp.getUserName() == userName && tmp.getPassHash().equals(User.hashPass(pass))) {
				isLogin = true;
				if (tmp instanceof Admin) {					
					isAdmin = true;
				} else {
					isAdmin = false;
				}
				currentUser = tmp;
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
		// TODO(Peng) creatFlight
		 if (isLogin&&isAdmin){
			dataManager.flights.add(new Flight(flightName,startTime, arriveTime,dataManager.getCityByID(startCityID),dataManager.getCityByID(arriveCityID),
				price,seatCapacity));
		} else { throw new PermissionDeniedException();
		}
	}
	
	
	public Flight getFlight(int flightID) throws PermissionDeniedException { //give you flight to change freely
		if (isLogin && isAdmin) {
			return dataManager.getFlightByID(flightID);			
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public boolean deleteFlight(int flightID) throws PermissionDeniedException, StatusUnavailableException	 {
		/* TODO(Peng) deleteFlight
		 * **be sure to delete flight from the city**
		 */
		if (isLogin && isAdmin) {
			Flight f =dataManager.getFlightByID(flightID);
			
			if (f.getFlightStatus()==FlightStatus.UNPUBLISHED){
				dataManager.flights.remove(f);
			}else {throw new StatusUnavailableException();}
		}else {throw new PermissionDeniedException();}
		return false; 
	}
	
	public void queryFlight() {
		// XXX I think this can be changed to a class
	}
	
	public void superQuery() { 
		// XXX I think this can be changed to a class extends queryFlight
	}
	
	public void addAdmin(String userName, String password) throws PermissionDeniedException {
		// TODO(Peng) addAdmin
		if (isLogin){
            if (isAdmin){
                 password=User.hashPass(password);     	
            	 dataManager.users.add(new Admin(userName, password));
         }
        else  throw new PermissionDeniedException();}
    		
	}
	
	public void addCity(String cityName) throws PermissionDeniedException{
		//TODO(Zhu) addCity
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
				Flight flight = getFlight(flightID);
				flight.addPassager((Passenger) currentUser);
			}
		} else {
			throw new PermissionDeniedException();
		}
	}
	
	public boolean unsubscribeFlight(int flightID) throws PermissionDeniedException { //return false when no flight is found
		//TODO(Pegn) unsubscribeFlight
		if (isLogin) {
			if (!isAdmin) {
				Passenger user=(Passenger) getCurrentUser();
				
				
		}
		   }
		       
		return false;	
		
		
	
}	
}
