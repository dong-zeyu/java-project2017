package flight;

import java.util.ArrayList;
import java.util.Date;

import user.Passenger;

public class Flight {
	
	private String flightID;
	private Date startTime;
	private Date arriveTime;
	private City startCity;
	private City arriveCity;
	private int price;
	private int seatCapacity;
	private FlightStatus flightStatus;
	private ArrayList<Passenger> passagers;
	
	public Flight(String flightID, Date startTime, Date arriveTime, City startCity, City arriveCity, int price,
			int seatCapacity, FlightStatus flightStatus) {
		passagers = new ArrayList<>();
		this.flightID = flightID;
		this.startTime = startTime;
		this.arriveTime = arriveTime;
		this.startCity = startCity;
		this.arriveCity = arriveCity;
		this.price = price;
		this.seatCapacity = seatCapacity;
		this.flightStatus = FlightStatus.UNPUBLISHED;
	}
	/* TODO
	 * getter and setter are generated automatically, and need mortifying
	 * basically the restriction is flightStatus
	 * if force to change, throw StatusUnavailableException("${CurrentStatus}").
	 */
	public String getFlightID() {
		return flightID;
	}

	public void setFlightID(String flightID) {
		this.flightID = flightID;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
	}

	public City getStartCity() {
		return startCity;
	}

	public void setStartCity(City startCity) {
		this.startCity = startCity;
	}

	public City getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(City arriveCity) {
		this.arriveCity = arriveCity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getSeatCapacity() {
		return seatCapacity;
	}

	public void setSeatCapacity(int seatCapacity) {
		this.seatCapacity = seatCapacity;
	}

	public FlightStatus getFlightStatus() {
		return flightStatus;
	}

	public void setFlightStatus(FlightStatus flightStatus) {
		this.flightStatus = flightStatus;
	}

	public ArrayList<Passenger> getPassagers() {
		return passagers;
	}

	public void addPassager(Passenger passager) {
		//TODO
	}
	
	
}
