package main;

import java.util.ArrayList;

import flight.City;
import flight.Flight;
import user.Admin;
import user.Passenger;
import user.User;

public class MainServer {
	
	ArrayList<Admin> admins = new ArrayList<>();
	ArrayList<Passenger> passengers = new ArrayList<>();
	ArrayList<Flight> flights = new ArrayList<>();
	ArrayList<City> cities = new ArrayList<>();
	DataManager dataManager;
	User currentUser;
	private boolean isLogin;
	private boolean isAdmin;
	
	public MainServer() {
		isLogin = false;
		isAdmin = false;
		// TODO init admins, passengers, flights and cities. Set current user
	}
	
	public boolean Login(String userName, String pass) {
		// TODO set isAdmin isLogin
		return false;
	}
	
	public boolean isLogin() {
		return isLogin;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
	/*
	 * TODO main function(search, add, ...) at here, each should decide whether isLogin and isAdmin
	 */
}
