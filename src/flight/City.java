package flight;

public class City {
	
	private static int ID = 0; //ID is just the count of cities
	private int cityID; //cityID needn't changing
	public String cityName; //it can be public
	
	public City(String cityName) {
		this.cityName = cityName;
		this.cityID = ID;
		ID++;
	}

	public int getCityID() {
		return cityID;
	}

}
