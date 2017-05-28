package data;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2450079723007813038L;
	public ArrayList<User> users;
	public ArrayList<Flight> flights;
	public ArrayList<City> cities;
	public ArrayList<FlightDaemon> flightDaemons;
	
	public void readIn(ArrayList<User> users, ArrayList<Flight> flights, ArrayList<City> cities,
			ArrayList<FlightDaemon> flightDaemons) {
		this.users = users;
		this.flights = flights;
		this.cities = cities;
		this.flightDaemons = flightDaemons;
	}

	public ArrayList<ArrayList<?>> readOut() {
		ArrayList<ArrayList<?>> arrayList = new ArrayList<>();
		arrayList.add(users);
		arrayList.add(cities);
		arrayList.add(flights);
		arrayList.add(flightDaemons);
		return arrayList;
	}
}