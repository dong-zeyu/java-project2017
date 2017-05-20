package data;

import java.util.ArrayList;

public class City {
	
	public static int ID = 0; //ID is just the count of cities
	private int cityID; //cityID needn't changing
	private String cityName;
	protected ArrayList<FlightDaemon> flightsIn; // flights end in this city
	protected ArrayList<FlightDaemon> flightsOut; // flights start in this city
	
	public City(String cityName) {
		flightsIn = new ArrayList<>();
		flightsOut = new ArrayList<>();
		this.cityName = cityName;
		this.cityID = ID;
		ID++;
	}
	
	@Override
	public int hashCode() {
		return cityName.hashCode();
	}
	
	@Override
	public String toString() {
		return cityName;
	}

	public int getCityID() {
		return cityID;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public ArrayList<FlightDaemon> getFlightsIn() {
		return (ArrayList<FlightDaemon>) flightsIn.clone();
	}

	public ArrayList<FlightDaemon> getFlightsOut() {
		return (ArrayList<FlightDaemon>) flightsOut.clone();
	}
	
}
