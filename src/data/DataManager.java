package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/** 
 * This is to provide a access to write file
 * use constructor DataManager(MainServer server) to trace
 * use saveData() to save to file
 */
public class DataManager {
	
	// DONE(Dong) This class is all of my job :(
	public ArrayList<User> users;
	public ArrayList<Flight> flights;
	public ArrayList<City> cities;
	public ArrayList<FlightDaemon> flightDaemons;
	public static final long CHECKING_INTERVAL = 1000l; // 1 second
	public static final long DAY_OF_CREATE = 30*24*3600*1000l; // 30 days
	public static final long INTERVAL_TO_CREATE = 3600*1000l; // 1 hour
	public static final long TIME_TO_TERMINATE = 2*3600*1000l; // 2 hours
	public static final long TIME_TO_PUBLISH = 15*24*3600*1000l; // 15 days
//	private final String filename = "data.xml";
	private final String filename = "data";
	private File file;
//	private Doc doc;
	private Timer timer;
	Data data;
	
	class ChangeFlight extends TimerTask {

		@Override
		public void run() {
			Date now = new Date();
			for (Flight flight : flights) {
				if (flight.isDaemon()) {
					if (flight.getStartTime().getTime() - now.getTime() <= TIME_TO_TERMINATE) {
						flight.flightStatus = FlightStatus.TERMINATE;
						flight.setDaemon(false);
					} else if (flight.getStartTime().getTime() - now.getTime() <= TIME_TO_PUBLISH) {
						if (flight.flightStatus == FlightStatus.UNPUBLISHED) {
							flight.flightStatus = FlightStatus.AVAILABLE;							
						}
					} 
				}				
			}
		}
		
	}
	
	class CreateFlight extends TimerTask {

		@Override
		public void run() {
			for (FlightDaemon flightDaemon : flightDaemons) {
				if (!flightDaemon.status) {
					break;
				}
				long now = new Date().getTime();
				long end = now + DAY_OF_CREATE;
				if (end < flightDaemon.getStartTime().getTime()) {
					continue;
				}
				for (long i = flightDaemon.getStartTime().getTime(); i < end; i+=flightDaemon.getPeriod()) {
					boolean isCreated = false;
					for (Flight flight : flightDaemon.children) {
						if (flight.getStartTime().getTime() == i) {
							isCreated = true;
							break;
						}
					}
					if (!isCreated) {
						Flight flight = new Flight(
								flightDaemon.getFlightName(), 
								new Date(i), 
								new Date(i + flightDaemon.getArriveTime().getTime() - flightDaemon.getStartTime().getTime()), 
								flightDaemon.getStartCity(), 
								flightDaemon.getArriveCity(), 
								flightDaemon.getPrice(), 
								flightDaemon.getSeatCapacity(), 
								flightDaemon.getDistance());
						flight.setDaemon(true);
						flights.add(flight);
						flightDaemon.children.add(flight);
					}
				}
			}
		}
		
	}
	
	public void stop() {
		timer.cancel();
		try {
			saveData();
		} catch (IOException e) {
			System.out.println("Saving data faild, continue to stop...");
		}
	}
	
	public void saveData() throws IOException {
		data.readIn(users, flights, cities, flightDaemons);
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		stream.writeObject(data);
		stream.flush();
		stream.close();
	}

	public DataManager() {
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Read/write data error!");
			System.exit(-1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		timer = new Timer(false);
		timer.schedule(new ChangeFlight(), CHECKING_INTERVAL, CHECKING_INTERVAL);
		timer.schedule(new CreateFlight(), 0, INTERVAL_TO_CREATE);
	}
	
	public void init() throws IOException, ClassNotFoundException {
		file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
			data = new Data();
			users = new ArrayList<>();
			flightDaemons = new ArrayList<>();
			flights = new ArrayList<>();
			cities = new ArrayList<>();
			Admin admin = new Admin("Admin", "admin");
			users.add(admin);
			City shenz = new City("Shenzhen");
			City beij = new City("Beijing");
			City zhenz = new City("Zhengzhou");
			City shangh = new City("Shanghai");
			City Wuh = new City("Wuhan");
			City Nanc = new City("Nanchang");
			City Hangz = new City("Hangzhou");
			cities.add(zhenz);
			cities.add(beij);
			cities.add(shenz);
			cities.add(shangh);
			cities.add(Wuh);
			cities.add(Nanc);
			cities.add(Hangz);
			FlightDaemon flight1 = new FlightDaemon("A001", Flight.calendar(2017, 5, 1, 12, 30, 0),
					Flight.calendar(2017, 5, 1, 14, 40, 0), 24 * 3600 * 1000, shenz, beij, 1200, 120, 1800000);
			FlightDaemon flight2 = new FlightDaemon("A002", Flight.calendar(2017, 5, 1, 9, 12, 0),
					Flight.calendar(2017, 5, 1, 10, 42, 0), 7 * 24 * 3600 * 1000, beij, shenz, 1200, 120, 1800000);
			FlightDaemon flight3 = new FlightDaemon("A003", Flight.calendar(2017, 5, 1, 16, 12, 00),
					Flight.calendar(2017, 5, 1, 16, 52, 00), 24 * 3600 * 1000, zhenz, shenz, 1200, 120, 1200000);
			FlightDaemon flight4 = new FlightDaemon("A004", Flight.calendar(2017, 5, 1, 10, 55, 00),
					Flight.calendar(2017, 5, 1, 14, 32, 00), 7 * 24 * 3600 * 1000, shenz, zhenz, 1200, 120, 1200000);
			FlightDaemon flight6 = new FlightDaemon("A006", Flight.calendar(2017, 5, 1, 22, 46, 00),
					Flight.calendar(2017, 5, 2, 00, 10, 00), 24 * 3600 * 1000, zhenz, Nanc, 250, 300, 950000);
			FlightDaemon flight7 = new FlightDaemon("A007", Flight.calendar(2017, 5, 1, 23, 46, 00),
					Flight.calendar(2017, 5, 2, 00, 10, 00), 24 * 3600 * 1000, Wuh, Hangz, 900, 90, 470000);
			FlightDaemon flight9 = new FlightDaemon("A009", Flight.calendar(2017, 5, 1, 11, 46, 00),
					Flight.calendar(2017, 5, 1, 13, 10, 00), 24 * 3600 * 1000, shangh, Hangz, 870, 100, 240000);
			FlightDaemon flight10 = new FlightDaemon("A010", Flight.calendar(2017, 5, 1, 17, 46, 00),
					Flight.calendar(2017, 5, 1, 19, 10, 00), 24 * 3600 * 1000, shenz, Hangz, 870, 100, 660000);
			FlightDaemon flight11 = new FlightDaemon("A011", Flight.calendar(2017, 5, 1, 17, 46, 00),
					Flight.calendar(2017, 5, 1, 19, 10, 00), 7 * 24 * 3600 * 1000, Hangz, shenz, 900, 100, 660000);
			FlightDaemon flight12 = new FlightDaemon("A012", Flight.calendar(2017, 5, 1, 17, 46, 00),
					Flight.calendar(2017, 5, 1, 19, 30, 00), 24 * 3600 * 1000, Hangz, shangh, 1130, 100, 240000);
			FlightDaemon flight14 = new FlightDaemon("A014", Flight.calendar(2017, 5, 1, 15, 46, 00),
					Flight.calendar(2017, 5, 1, 16, 40, 00), 24 * 3600 * 1000, shenz, Wuh, 780, 120, 780000);
			FlightDaemon flight15 = new FlightDaemon("A015", Flight.calendar(2017, 5, 1, 15, 46, 00),
					Flight.calendar(2017, 5, 1, 18, 40, 00), 24 * 3600 * 1000, Hangz, Wuh, 860, 120, 340000);
			flightDaemons.add(flight1);
			flightDaemons.add(flight2);
			flightDaemons.add(flight3);
			flightDaemons.add(flight4);
			flightDaemons.add(flight6);
			flightDaemons.add(flight7);
			flightDaemons.add(flight9);
			flightDaemons.add(flight10);
			flightDaemons.add(flight11);
			flightDaemons.add(flight12);
			flightDaemons.add(flight14);
			flightDaemons.add(flight15);
		} else {
			readData();
		}
	}

	@SuppressWarnings("unchecked")
	public void readData() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
		data = (Data) stream.readObject();
		stream.close();
		ArrayList<ArrayList<?>> list = data.readOut();
		users = (ArrayList<User>) list.get(0);
		cities = (ArrayList<City>) list.get(1);
		flights = (ArrayList<Flight>) list.get(2);
		flightDaemons = (ArrayList<FlightDaemon>) list.get(3);
	}

	public Flight getFlightByID(int flightID) {
		// DONE(Zhu) searchFlightByID
		for (Flight flight : flights) {
			if(flight.getFlightID()==flightID){
				return flight;
			}
		}
		return null;
	}
	
	public User getUserByID(int userID) {
		// DONE(Zhu) searchUserByID
		for (User user : users) {
			if(user.getID()==userID){
				return user;
			}
		}
		return null;
	}
	
	public City getCityByID(int cityID) {
		// DONE(Zhu) searchCityByID
		for (City city : cities) {
			if (city.getCityID()==cityID) {
				return city;
			}
		}
		return null;
	}

	public FlightDaemon getFlightDaemonByID(int flightID) {
		for (FlightDaemon daemon : flightDaemons) {
			if (daemon.getFlightDaemonID()==flightID) {
				return daemon;
			}
		}
		return null;
	}	
}
