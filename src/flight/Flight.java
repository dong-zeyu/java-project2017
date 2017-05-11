package flight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import exceptions.StatusUnavailableException;
import main.DataManager;
import user.Passenger;

public class Flight {
	
	public static int ID = 0;
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
	
	@Override
	public String toString() {
		return flightName;
	}
	
	@Override
	public int hashCode() {
        int hashCode = 1;
        hashCode = 31*hashCode + flightName.hashCode();
        hashCode = 31*hashCode + startCity.hashCode();
        hashCode = 31*hashCode + startTime.hashCode();
        hashCode = 31*hashCode + arriveCity.hashCode();
        hashCode = 31*hashCode + arriveTime.hashCode();
        hashCode = 31*hashCode + flightStatus.hashCode();
        hashCode = 31*hashCode + ((Integer)price).hashCode();
        hashCode = 31*hashCode + ((Integer)seatCapacity).hashCode();
        return hashCode;
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
	/* DONE(Zhu) get and set in Flight
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
		if(flightStatus==FlightStatus.UNPUBLISHED){
			this.flightName = flightName;
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) throws StatusUnavailableException {
		if(flightStatus==FlightStatus.UNPUBLISHED){
			this.startTime = startTime;
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public Date getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(Date arriveTime) throws StatusUnavailableException {
		if(flightStatus==FlightStatus.UNPUBLISHED){
			this.arriveTime = arriveTime;
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public City getStartCity() {
		return startCity;
	}

	public void setStartCity(City startCity) throws StatusUnavailableException {
		if(flightStatus==FlightStatus.UNPUBLISHED){
			this.startCity = startCity;
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public City getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(City arriveCity) throws StatusUnavailableException {
		if(flightStatus==FlightStatus.UNPUBLISHED){
			this.arriveCity = arriveCity;
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) throws StatusUnavailableException {
		if(flightStatus!=FlightStatus.TERMINATE){
			this.price = price;
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public int getSeatCapacity() {
		return seatCapacity;
	}

	public void setSeatCapacity(int seatCapacity) throws StatusUnavailableException {
		if(flightStatus!=FlightStatus.TERMINATE){
			// FIXME(Zhu) you should consider more in changing seat capacity
			this.seatCapacity = seatCapacity;
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
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
		/* DONE(Zhu) addPassager
		 * you should generate and add order in this method meanwhile
		 * for my convenience
		 * and check for status
		 */
		// FIXME(Zhu) take care of the comment above!
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
		/* DONE(Zhu) removePassenger
		 * you should remove in this method meanwhile
		 * and check for status
		 * XXX(Zhu) needs review
		 */
		if(flightStatus!=FlightStatus.TERMINATE)
			return passagers.remove(passenger);
		else
			throw new StatusUnavailableException();
	}
	
}
