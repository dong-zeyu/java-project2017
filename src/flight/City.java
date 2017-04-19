package flight;

public class City {
	
	private int cityID; //cityID needn't changing
	public String cityName;
	
	public City(int cityID, String cityName) {
		this.cityID = cityID;
		this.cityName = cityName;
	}

	public int getCityID() {
		return cityID;
	}

}
