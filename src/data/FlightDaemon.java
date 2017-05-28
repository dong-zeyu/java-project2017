package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Predicate;

import exceptions.StatusUnavailableException;

public class FlightDaemon implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -462004565055095904L;
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
	protected boolean status;
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
		status = true;
		children = new ArrayList<>();
		flightDaemonID = FlightDaemon.ID;
		ID++;
		startCity.flightsOut.add(this);
		arriveCity.flightsIn.add(this);
	}

	@Override
	public String toString() {
		return String.format("%d\t%s\t%-8s\t%-8s\t%s\t%dmin\t%dday\t%d\t%d",
				flightDaemonID,
				flightName,
				startCity,
				arriveCity,
				startTime,
				(arriveTime.getTime() - startTime.getTime())/60000,
				period/(24*3600*1000),
				price,
				seatCapacity)
				 + (status ? "" : "\tdeleted");
	}
	
	@Override
	public int hashCode() {
        int hashCode = 1;
        hashCode = 31*hashCode + flightName.hashCode();
        hashCode = 31*hashCode + startCity.hashCode();
        hashCode = 31*hashCode + startTime.hashCode();
        hashCode = 31*hashCode + arriveCity.hashCode();
        hashCode = 31*hashCode + arriveTime.hashCode();
        hashCode = 31*hashCode + ((Integer)price).hashCode();
        hashCode = 31*hashCode + ((Integer)seatCapacity).hashCode();
        hashCode = 31*hashCode + ((Boolean)status).hashCode();
        return hashCode;
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
				if (flight.isDaemon()) {
					flight.setFlightName(flightName);
				}
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		long shift = startTime.getTime() - this.startTime.getTime();
		this.startTime = startTime;
		for (Flight flight : children) {
			try {
				if (flight.isDaemon()) {
					flight.setStartTime(new Date(flight.getStartTime().getTime() + shift));
				}
			} catch (StatusUnavailableException e) { /* ignored */ }		
		}
	}

	public Date getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(Date arriveTime) {
		long shift = arriveTime.getTime() - this.arriveTime.getTime();		
		this.arriveTime = arriveTime;
		for (Flight flight : children) {
			try {
				if (flight.isDaemon()) {
					flight.setArriveTime(new Date(flight.getArriveTime().getTime() + shift));
				}
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
				if (flight.isDaemon()) {
					flight.setStartCity(startCity);
				}
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
				if (flight.isDaemon()) {
					flight.setArriveCity(arriveCity);
				}
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
				if (flight.isDaemon()) {
					flight.setPrice(price);
				}
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
				if (flight.isDaemon()) {
					flight.setSeatCapacity(seatCapacity);
				}
			} catch (StatusUnavailableException e) { /* ignored */ }
		}
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
		for (Flight flight : children) {
			if (flight.isDaemon()) {
				flight.setDistance(distance);
			}
		}
	}
	
	public boolean getStatus() {
		return status;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Flight> getChildren() {
		return (ArrayList<Flight>) children.clone();
	}

	public void removeFlight() {
		children.removeIf(new Predicate<Flight>() {

			@Override
			public boolean test(Flight t) {
				return t.flightStatus == FlightStatus.UNPUBLISHED;
			}
		});
		status = false;
	}

}
