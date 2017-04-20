package flight;

import java.util.ArrayList;

public class City {
	
	private static int ID = 0; //ID is just the count of cities
	private int cityID; //cityID needn't changing
	public String cityName; //it can be public
	public ArrayList<Flight> flightsIn; // flights end in this city
	public ArrayList<Flight> flightsOut; // flights start in this city
	
	public City(String cityName) {
		flightsIn = new ArrayList<>();
		flightsOut = new ArrayList<>();
		this.cityName = cityName;
		this.cityID = ID;
		ID++;
	}

	public int getCityID() {
		return cityID;
	}

}
