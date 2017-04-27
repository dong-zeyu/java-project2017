package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import flight.City;
import flight.Flight;
import user.Admin;
import user.Order;
import user.Passenger;
import user.User;

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
 * 		<cityname></cityname>
 * 	</city>
 * </root>
 */
public class DataManager {
	
	// TODO(Dong) This class is all of my job :(
	public ArrayList<User> users = new ArrayList<>();
	public ArrayList<Flight> flights = new ArrayList<>();
	public ArrayList<City> cities = new ArrayList<>();
	public static int SYNC_INTERVAL = 120; // unit: (s)
	private final String filename = "data.xml";
	private File file;
	
	public DataManager() {
		try {
			init(true);
		} catch (IOException | ParserConfigurationException e) {
			try {
				System.out.println("Read file error!");
				init(false);
			} catch (IOException | ParserConfigurationException e1) {}
		}
		
	}
	
	public Flight getFlightByID(int flightID) {
		// TODO(Zhu) searchFlightByID
		return null;
	}
	
	public User getUserByID(int userID) { // XXX whether should we return User?
		// TODO(Zhu) searchUserByID
		return null;
	}
	
	public City getCityByID(int CityID) {
		// TODO(Zhu) searchCityByID
		return null;
	}
	
	public void saveData() throws IOException, ParserConfigurationException {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = document.createElement("root");
		document.appendChild(root);
		Element user = document.createElement("user");
		Element flight = document.createElement("flight");
		Element city = document.createElement("city");
		root.appendChild(user);
		root.appendChild(city);
		root.appendChild(flight);
		//Users
		Element egga;
		Element eggp;
		egga = document.createElement("admin");
		egga.appendChild(document.createElement("username"));
		egga.appendChild(document.createElement("passhash"));
		eggp = document.createElement("passenger");
		eggp.appendChild(document.createElement("username"));
		eggp.appendChild(document.createElement("passhash"));
		eggp.appendChild(document.createElement("idnumber"));
		eggp.appendChild(document.createElement("order"));
		for (User user0 : users) {
			if (user0 instanceof Admin) {
				Admin admin = (Admin) user0;
				NodeList tmp = egga.cloneNode(true).getChildNodes();
				tmp.item(0).appendChild(document.createTextNode(admin.getUserName()));
				tmp.item(1).appendChild(document.createTextNode(admin.getPassHash()));
				user.appendChild(tmp.item(0).getParentNode());				
			} else if (user0 instanceof Passenger) {
				Passenger passenger = (Passenger) user0;
				NodeList tmp = eggp.cloneNode(true).getChildNodes();
				tmp.item(0).appendChild(document.createTextNode(passenger.getUserName()));
				tmp.item(1).appendChild(document.createTextNode(passenger.getPassHash()));
				tmp.item(2).appendChild(document.createTextNode(passenger.getIdentityID()));
				Element otmp = (Element) tmp.item(4);
				Element oegg = document.createElement("item");
				oegg.appendChild(document.createElement("flightid"));
				oegg.appendChild(document.createElement("seat"));
				oegg.appendChild(document.createElement("date"));
				oegg.appendChild(document.createElement("status"));
				for (Order order : passenger.getOrderList()) {
					NodeList item = oegg.cloneNode(true).getChildNodes();
					item.item(0).appendChild(document.createTextNode(String.valueOf(order.getFlight().getFlightID())));
					item.item(1).appendChild(document.createTextNode(String.valueOf(order.getSeat())));
					item.item(2).appendChild(document.createTextNode(String.valueOf(order.getCreatDate().getTime())));
					item.item(3).appendChild(document.createTextNode(String.valueOf(order.getStatus().name())));
					otmp.appendChild(item.item(0).getParentNode());
				}
				user.appendChild(tmp.item(0).getParentNode());
			}
		}
		//flight
		Element egg;
		egg = document.createElement("item");
		egg.appendChild(document.createElement("flightname"));
		egg.appendChild(document.createElement("starttime"));
		egg.appendChild(document.createElement("arrivetime"));
		egg.appendChild(document.createElement("startcity"));
		egg.appendChild(document.createElement("arrivecity"));
		egg.appendChild(document.createElement("price"));
		egg.appendChild(document.createElement("seatcapacity"));
		egg.appendChild(document.createElement("status"));
		for (Flight f : flights) {
			NodeList tmp = egg.cloneNode(true).getChildNodes();
			tmp.item(0).appendChild(document.createTextNode(f.getFlightName()));
			tmp.item(1).appendChild(document.createTextNode(String.valueOf(f.getStartTime().getTime())));
			tmp.item(2).appendChild(document.createTextNode(String.valueOf(f.getArriveTime().getTime())));
			tmp.item(3).appendChild(document.createTextNode(f.getStartCity().getCityName()));
			tmp.item(4).appendChild(document.createTextNode(f.getArriveCity().getCityName()));
			tmp.item(5).appendChild(document.createTextNode(String.valueOf(f.getPrice())));
			tmp.item(6).appendChild(document.createTextNode(String.valueOf(f.getSeatCapacity())));
			tmp.item(7).appendChild(document.createTextNode(f.getFlightStatus().name()));
			flight.appendChild(tmp.item(0).getParentNode());
		}
		//city
		for (City c : cities) {
			city.appendChild(document.createElement("item"))
				.appendChild(document.createTextNode(c.getCityName()));
		}
		saveDocument(document);
	}

	private void init(boolean isCreate) throws IOException, ParserConfigurationException {
		file = new File(filename);
		if (!file.exists()) {
			if (isCreate) {				
				file.createNewFile();
			}
			Admin admin = new Admin("Admin", "admin");
			users.add(admin);
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
			// TODO(Zhu) finish the remain 15 flight
			if (isCreate) {
				saveData();
			}
		} else {
			readData();
		}
	}

	private void readData() {
		
	}
	
	private void saveDocument(Node document) {
        try {
        	Transformer transformer = TransformerFactory.newInstance().newTransformer();
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        	DOMSource source=new DOMSource();
        	source.setNode(document);
        	StreamResult result=new StreamResult();
        	result.setOutputStream(new FileOutputStream(file));
			transformer.transform(source, result);
		} catch (TransformerException e) {
		} catch (FileNotFoundException e) {
		}
	}
		
}
