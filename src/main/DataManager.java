package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

import flight.City;
import flight.Flight;
import user.Admin;
import user.Passenger;

/** 
 * This is to provide a access to write file
 * use constructor DataManager(MainServer server) to trace
 * use saveData() to save to file
 */
public class DataManager extends TimerTask {
	
	// TODO(Dong) This class is all of my job :(
	public ArrayList<Admin> admins = new ArrayList<>();
	public ArrayList<Passenger> passengers = new ArrayList<>();
	public ArrayList<Flight> flights = new ArrayList<>();
	public ArrayList<City> cities = new ArrayList<>();
	public static int SYNC_INTERVAL = 120; // unit: (s)
	private final String filename = "data.xml";
	private File file;
	
	public DataManager() {
		try {
			init();
		} catch (IOException e) {
			try {
				init();
			} catch (IOException e1) {
				System.out.println("Read flie error!");
				
			}
		}
	}
	
	public void saveData() {
		
	}

	private void init() throws IOException {
		file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
			Admin admin = new Admin("Admin", "admin");
			admins.add(admin);
			City shenz = new City("Shenzhen");
			City beij = new City("Beijing");
			City zhenz = new City("Zhengzhou");
			cities.add(zhenz);
			cities.add(beij);
			cities.add(shenz);
			Flight flight1 = new Flight("A001",
					new Date(2017, 3, 1, 14, 10, 0),
					new Date(2017, 3, 1, 16, 0, 0), shenz, beij, 1200, 120);
			Flight flight2 = new Flight("A002",
					new Date(2017, 3, 1, 14, 10, 0),
					new Date(2017, 3, 1, 16, 0, 0), beij, shenz, 1200, 120);
			Flight flight3 = new Flight("A003",
					new Date(2017, 3, 1, 14, 10, 0),
					new Date(2017, 3, 1, 16, 0, 0), zhenz, shenz, 1200, 120);
			flights.add(flight1);
			flights.add(flight2);
			flights.add(flight3);
			saveData();
		}
	}

	private String toXMLString() {
		return null;
	}
	
	@Override
	public void run() {
		
	}
	
}
