package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import flight.City;
import flight.Flight;
import user.Admin;
import user.Passenger;

/** 
 * This is to provide a access to write file
 * use constructor DataManager(MainServer server) to trace
 * use saveData() to save to file
 */
/* file format:
 * <root>
 * 	<user>
 * 		<admin>
 * 			<username></username>
 * 			<passhash></passhash>
 * 		</admin>
 * 		<passenger>
 * 			<username></username>
 * 			<passhash></passhash>
 * 			<idnumber></idnumber>
 * 			<order>
 * 				<item>
 * 					<flightid></flightid>
 * 					<seat></seat>
 * 					<date></date>
 * 					<status></status>
 * 				</item>
 * 			</order>
 * 		</passenger>
 * 	</user>
 * 	<flight>
 * 		<item>
 *			<flightname></flightname>
 *			<starttime><starttime>
 *			<arrivetime><arrivetime>
 *			<startcity></startcity>
 *			<arrivecity></arrivecity>
 *			<price></price>
 *			<seatcapacity></seatcapacity>
 *			<status></status>
 * 		</item>
 * 	</flight>
 * 	<city>
 * 		<item>
 * 			<cityname></cityname>
 * 		</item>
 * 	</city>
 * </root>
 */
public class DataManager {
	
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
			init(true);
		} catch (IOException e) {
			try {
				System.out.println("Read flie error!");
				init(false);
			} catch (IOException e1) {}
		}
		
	}
	
	public void saveData() throws IOException {
		FileWriter writer = new FileWriter(file);
		BufferedWriter bWriter = new BufferedWriter(writer);
		bWriter.write(toXMLString());
	}

	private void init(boolean isCreate) throws IOException {
		file = new File(filename);
		if (!file.exists()) {
			if (isCreate) {				
				file.createNewFile();
			}
			Admin admin = new Admin("Admin", "admin");
			admins.add(admin);
			City shenz = new City("Shenzhen");
			City beij = new City("Beijing");
			City zhenz = new City("Zhengzhou");
			cities.add(zhenz);
			cities.add(beij);
			cities.add(shenz);
			Flight flight1 = new Flight("A001",
					Flight.calendar(2017, 4, 1, 9, 30, 0),
					Flight.calendar(2017, 4, 1, 10, 40, 0), shenz, beij, 1200, 120);
			Flight flight2 = new Flight("A002",
					Flight.calendar(2017, 5, 2, 9, 12, 0),
					Flight.calendar(2017, 5, 2, 10, 42, 0), beij, shenz, 1200, 120);
			Flight flight3 = new Flight("A003",
					Flight.calendar(2017, 3, 3, 16, 12, 00),
					Flight.calendar(2017, 3, 3, 16, 52, 00), zhenz, shenz, 1200, 120);
			flights.add(flight1);
			flights.add(flight2);
			flights.add(flight3);
			if (isCreate) {
				saveData();				
			}
		}
	}

	private String toXMLString() {
		return null;
	}
	
}
