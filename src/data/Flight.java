package data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import exceptions.StatusUnavailableException;

public class Flight implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4984381831014139467L;
	public static int ID = 0;
	private int flightID;
	private String flightName;
	private Date startTime;
	private Date arriveTime;
	private City startCity;
	private City arriveCity;
	private int price;
	private int seatCapacity;
	protected FlightStatus flightStatus;
	private HashMap<Passenger, Integer> passagers;
	private int distance;
	private boolean isDaemon;
	
	public Flight(String flightName, Date startTime, Date arriveTime, City startCity, City arriveCity, int price,
			int seatCapacity, int distance) {
		passagers = new HashMap<>();
		this.flightName = flightName;
		this.startTime = startTime;
		this.arriveTime = arriveTime;
		this.startCity = startCity;
		this.arriveCity = arriveCity;
		this.price = price;
		this.seatCapacity = seatCapacity;
		this.distance = distance;
		this.flightStatus = FlightStatus.UNPUBLISHED;
		isDaemon = true;
		flightID = Flight.ID;
		ID++;
	}
	
	@Override
	public String toString() {
		return String.valueOf(flightID) + "\t" +
				flightName + "\t" + 
				((startCity.toString().length() < 8) ? (startCity.toString() + "\t") : startCity.toString()) + "\t" +
				((arriveCity.toString().length() < 8) ? (arriveCity.toString() + "\t") : arriveCity.toString()) + "\t" +
				startTime.toString() + "\t" +
				arriveTime.toString() + "\t" +
				String.valueOf(price) + "\t" +
				String.valueOf(seatCapacity - passagers.size()) + "\t" +
				flightStatus.name();
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

	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
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
			// DONE(Zhu) you should consider more in changing seat capacity
           if(flightStatus == FlightStatus.FULL &&
        		   this.seatCapacity< seatCapacity){
        	   this.seatCapacity=seatCapacity;
           }else{
        	   throw new StatusUnavailableException("Set Failed!");
           }    
		}else{
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public FlightStatus getFlightStatus() {
		return flightStatus;
	}
	
	/**
	 * read only, use add/remove to operate
	 * @return a clone of field passengers
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Passenger, Integer> passagers() {
		return (HashMap<Passenger, Integer>) passagers.clone();
	}
	
	protected HashMap<Passenger, Integer> getPassagers() {
		return passagers;
	}

	protected void addPassenger(Passenger passenger, int seat, boolean ignore) throws StatusUnavailableException {
		if (!ignore) {
			/* DONE(Zhu) addPassager
			 * you should generate and add order in this method meanwhile
			 * for my convenience
			 * and check for status
			 */
			if (flightStatus == FlightStatus.AVAILABLE) {
				passagers.put(passenger, seat);
				if (passagers.size() == seatCapacity) {
					flightStatus = FlightStatus.FULL;
				}
			} else {
				throw new StatusUnavailableException(flightStatus);
			}
		} else {
			passagers.put(passenger, seat);			
		}
	}
	
	protected void addPassenger(Passenger passenger, int seat) throws StatusUnavailableException {
		addPassenger(passenger, seat, false);
	}
	
	protected void addPassenger(Passenger passenger) throws StatusUnavailableException {
		addPassenger(passenger, getAvailableSeat());
	}
	
	/**
	 * remove passenger from the passenger list
	 * @return return false when no one can found
	 * @throws StatusUnavailableException when status is TERMINATE, 
	 */
	protected boolean removePassenger(Passenger passenger) throws StatusUnavailableException {
		/* DONE(Zhu) removePassenger
		 * you should remove in this method meanwhile
		 * and check for status
		 * XXX(Zhu) needs review
		 */
		if(flightStatus!=FlightStatus.TERMINATE) {
			if (passagers.remove(passenger) != null) {
				if (passagers.size() < seatCapacity && flightStatus == FlightStatus.FULL) {
					flightStatus = FlightStatus.AVAILABLE;
				} else if (passagers.size() == seatCapacity) {
					flightStatus = FlightStatus.FULL;
				}
				return true;
			} else {
				return false;
			}
		} else
			throw new StatusUnavailableException(flightStatus);
	}

	public void publish() throws StatusUnavailableException {
		if (isDaemon) {
			throw new StatusUnavailableException("the status of this flight is under control of server!");
		}
		if (flightStatus == FlightStatus.UNPUBLISHED) {
			flightStatus = FlightStatus.AVAILABLE;
		} else {
			throw new StatusUnavailableException(flightStatus);
		}
	}

	public int getAvailableSeat() {
		Collection<Integer> seat = passagers.values();
		if (seat.size() == seatCapacity) {
			return -1;
		}
		Random random = new Random(this.hashCode() * this.hashCode());
		int result;
		do {
			result = random.nextInt(seatCapacity) + 1;			
		} while (seat.contains(result));
		return result;
	}

	protected boolean isDaemon() {
		return isDaemon;
	}

	protected void setDaemon(boolean isDaemon) {
		this.isDaemon = isDaemon;
	}

	public void delete() {
		isDaemon = false;
		flightStatus = FlightStatus.TERMINATE;
	}
	
}
