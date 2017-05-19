package data;

import java.util.ArrayList;
import java.util.Date;

import exceptions.StatusUnavailableException;

public class FlightDaemon {
	
	public static int ID = 0;
	private int flightDaemonID;
	private String flightName;
	private Date startTime;
	private Date arriveTime;
	private int period;
	private City startCity;
	private City arriveCity;
	private int price;
	private int seatCapacity;
	private int distance;
	protected ArrayList<Flight> children;

	public FlightDaemon(String flightName, Date startTime, Date arriveTime, int period, City startCity, City arriveCity, int price,
			int seatCapacity, int distance) {
		this.flightName = flightName;
		this.startTime = startTime;
		this.arriveTime = arriveTime;
		this.period = period;
		this.startCity = startCity;
		this.arriveCity = arriveCity;
		this.price = price;
		this.seatCapacity = seatCapacity;
		this.distance = distance;
		children = new ArrayList<>();
		flightDaemonID = FlightDaemon.ID;
		ID++;
	}

	public int getFlightDaemonID() {
		return flightDaemonID;
	}

	public void setFlightDaemonID(int flightDaemonID) {
		this.flightDaemonID = flightDaemonID;
	}

	public String getFlightName() {
		return flightName;
	}

	public void setFlightName(String flightName) {
		this.flightName = flightName;
		for (Flight flight : children) {
			try {
				flight.setFlightName(flightName);
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		for (Flight flight : children) {
			try {
				flight.setStartTime(startTime);
			} catch (StatusUnavailableException e) { /* ignored */ }		
		}
	}

	public Date getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
		for (Flight flight : children) {
			try {
				flight.setArriveTime(arriveTime);
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public City getStartCity() {
		return startCity;
	}

	public void setStartCity(City startCity) {
		this.startCity = startCity;
		for (Flight flight : children) {
			try {
				flight.setStartCity(startCity);
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public City getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(City arriveCity) {
		this.arriveCity = arriveCity;
		for (Flight flight : children) {
			try {
				flight.setArriveCity(arriveCity);
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
		for (Flight flight : children) {
			try {
				flight.setPrice(price);
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public int getSeatCapacity() {
		return seatCapacity;
	}

	public void setSeatCapacity(int seatCapacity) {
		this.seatCapacity = seatCapacity;
		for (Flight flight : children) {
			try {
				flight.setSeatCapacity(seatCapacity);
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
		for (Flight flight : children) {
			flight.setDistance(distance);
		}
	}
	
}
