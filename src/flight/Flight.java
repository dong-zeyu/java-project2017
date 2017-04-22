package flight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import exceptions.StatusUnavailableException;
import user.Passenger;

public class Flight {
	
	private static int ID = 0;
	private int flightID;
	private String flightName;
	private Date startTime;
	private Date arriveTime;
	private City startCity;
	private City arriveCity;
	private int price;
	private int seatCapacity;
	private FlightStatus flightStatus;
	private ArrayList<Passenger> passagers;
	
	public Flight(String flightName, Date startTime, Date arriveTime, City startCity, City arriveCity, int price,
			int seatCapacity) {
		passagers = new ArrayList<>();
		this.flightName = flightName;
		this.startTime = startTime;
		this.arriveTime = arriveTime;
		this.startCity = startCity;
		this.arriveCity = arriveCity;
		this.price = price;
		this.seatCapacity = seatCapacity;
		this.flightStatus = FlightStatus.UNPUBLISHED;
		flightID = Flight.ID;
		ID++;
		startCity.getFlightsOut().add(this);
		arriveCity.getFlightsIn().add(this);
	}
	
	public int getNumber() {
		return passagers.size();
	}
	
	public static Date calendar(int year, int month, int date, int hr, int min, int sec) {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+8:00"));
		calendar.clear();
		calendar.set(year, month - 1, date, hr, min, sec);
		return calendar.getTime();
	}
	/* TODO(Zhu) get and set in Flight
	 * getter and setter are generated automatically, and need mortifying
	 * basically the restriction is flightStatus(seeing requirement)
	 * if force to change, throw StatusUnavailableException(${CurrentStatus}).
	 */
	public int getFlightID() {
		return flightID;
	}
	
	public String getFlightName() {
		return flightName;
	}
	
	public void setFlightName(String flightName) throws StatusUnavailableException {
		this.flightName = flightName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) throws StatusUnavailableException {
		this.startTime = startTime;
	}

	public Date getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(Date arriveTime) throws StatusUnavailableException {
		this.arriveTime = arriveTime;
	}

	public City getStartCity() {
		return startCity;
	}

	public void setStartCity(City startCity) throws StatusUnavailableException {
		this.startCity = startCity;
	}

	public City getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(City arriveCity) throws StatusUnavailableException {
		this.arriveCity = arriveCity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) throws StatusUnavailableException {
		this.price = price;
	}

	public int getSeatCapacity() {
		return seatCapacity;
	}

	public void setSeatCapacity(int seatCapacity) throws StatusUnavailableException {
		this.seatCapacity = seatCapacity;
	}

	public FlightStatus getFlightStatus() {
		return flightStatus;
	}

	public void setFlightStatus(FlightStatus flightStatus) {
		this.flightStatus = flightStatus;
	}
	
	/**
	 * read only, use add/remove to operate
	 * @return a clone of field passengers
	 */
	public ArrayList<Passenger> getPassagers() {
		return (ArrayList<Passenger>) passagers.clone();
	}

	public void addPassager(Passenger passager) throws StatusUnavailableException {
		/* TODO(Zhu) addPassager
		 */
		if (flightStatus == FlightStatus.AVAILABLE) {
			 passagers.add(passager);			
		} else {
			throw new StatusUnavailableException(flightStatus);
		}
	}
	
	/**
	 * remove passenger from the passenger list
	 * @return return false when no one can found
	 * @throws StatusUnavailableException when status is TERMINATE, 
	 */
	public boolean removePassenger(Passenger passenger) throws StatusUnavailableException {
		// TODO(Zhu) removePassenger
		return false;
	}
	
}
