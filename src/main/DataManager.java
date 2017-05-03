package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.lang.model.util.Elements;
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
import org.xml.sax.SAXException;

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
 * 		<admin id=>
 * 			<username></username>
 * 			<passhash></passhash>
 * 		</admin>
 * 		<passenger id=>
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
 * 		<item id=>
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
 * 		<cityname id=></cityname>
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
			init();
		} catch (IOException e) {
			System.out.println("Read/write data error!");
			System.exit(-1);
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
	
	public void saveData() throws FileNotFoundException {
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = document.createElement("root");
			document.appendChild(root);
			Element euser = document.createElement("user");
			Element eflight = document.createElement("flight");
			Element ecity = document.createElement("city");
			root.appendChild(euser);
			root.appendChild(ecity);
			root.appendChild(eflight);
			//Users
			Element adminEgg;
			Element passengerEgg;
			adminEgg = document.createElement("admin");
			adminEgg.appendChild(document.createElement("username"));
			adminEgg.appendChild(document.createElement("passhash"));
			passengerEgg = document.createElement("passenger");
			passengerEgg.appendChild(document.createElement("username"));
			passengerEgg.appendChild(document.createElement("passhash"));
			passengerEgg.appendChild(document.createElement("idnumber"));
			passengerEgg.appendChild(document.createElement("order"));
			for (User user : users) {
				if (user instanceof Admin) {
					Admin admin = (Admin) user;
					Element node = (Element) adminEgg.cloneNode(true);
					node.setAttribute("uid", String.valueOf(admin.getID()));
					NodeList nodes = node.getChildNodes();
					nodes.item(0).appendChild(document.createTextNode(admin.getUserName()));
					nodes.item(1).appendChild(document.createTextNode(admin.getPassHash()));
					euser.appendChild(nodes.item(0).getParentNode());				
				} else if (user instanceof Passenger) {
					Passenger passenger = (Passenger) user;
					Element node = (Element) adminEgg.cloneNode(true);
					node.setAttribute("uid", String.valueOf(passenger.getID()));
					NodeList nodes = node.getChildNodes();
					nodes.item(0).appendChild(document.createTextNode(passenger.getUserName()));
					nodes.item(1).appendChild(document.createTextNode(passenger.getPassHash()));
					nodes.item(2).appendChild(document.createTextNode(passenger.getIdentityID()));
					Element orderEgg = document.createElement("item");
					orderEgg.appendChild(document.createElement("flightid"));
					orderEgg.appendChild(document.createElement("seat"));
					orderEgg.appendChild(document.createElement("date"));
					orderEgg.appendChild(document.createElement("status"));
					for (Order order : passenger.getOrderList()) {
						NodeList item = orderEgg.cloneNode(true).getChildNodes();
						item.item(0).appendChild(document.createTextNode(String.valueOf(order.getFlight().getFlightID())));
						item.item(1).appendChild(document.createTextNode(String.valueOf(order.getSeat())));
						item.item(2).appendChild(document.createTextNode(String.valueOf(order.getCreatDate().getTime())));
						item.item(3).appendChild(document.createTextNode(String.valueOf(order.getStatus().name())));
						nodes.item(4).appendChild(item.item(0).getParentNode());
					}
					euser.appendChild(nodes.item(0).getParentNode());
				}
			}
			adminEgg = null;
			passengerEgg = null;
			//flight
			Element flightEgg;
			flightEgg = document.createElement("item");
			flightEgg.appendChild(document.createElement("flightname"));
			flightEgg.appendChild(document.createElement("starttime"));
			flightEgg.appendChild(document.createElement("arrivetime"));
			flightEgg.appendChild(document.createElement("startcity"));
			flightEgg.appendChild(document.createElement("arrivecity"));
			flightEgg.appendChild(document.createElement("price"));
			flightEgg.appendChild(document.createElement("seatcapacity"));
			flightEgg.appendChild(document.createElement("status"));
			for (Flight f : flights) {
				Element node = (Element) adminEgg.cloneNode(true);
				node.setAttribute("fid", String.valueOf(f.getFlightID()));
				NodeList nodes = node.getChildNodes();
				nodes.item(0).appendChild(document.createTextNode(f.getFlightName()));
				nodes.item(1).appendChild(document.createTextNode(String.valueOf(f.getStartTime().getTime())));
				nodes.item(2).appendChild(document.createTextNode(String.valueOf(f.getArriveTime().getTime())));
				nodes.item(3).appendChild(document.createTextNode(f.getStartCity().getCityName()));
				nodes.item(4).appendChild(document.createTextNode(f.getArriveCity().getCityName()));
				nodes.item(5).appendChild(document.createTextNode(String.valueOf(f.getPrice())));
				nodes.item(6).appendChild(document.createTextNode(String.valueOf(f.getSeatCapacity())));
				nodes.item(7).appendChild(document.createTextNode(f.getFlightStatus().name()));
				eflight.appendChild(nodes.item(0).getParentNode());
			}
			flightEgg = null;
			//city
			for (City c : cities) {
				ecity.appendChild(document.createElement("item"))
					.appendChild(document.createTextNode(c.getCityName()));
			}
			saveDocument(document);
		} catch (ParserConfigurationException e) {
		}
	}

	private void init() throws IOException {
		try {
			file = new File(filename);
			if (!file.exists()) {	
				file.createNewFile();
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
				saveData();
			} else {
					readData();
			}
		} catch (FileNotFoundException | SAXException e) {
			file.delete();
			init();
		}
	}

	private void readData() throws SAXException, FileNotFoundException, IOException {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file));
			Element eflight = (Element) document.getElementsByTagName("flight").item(0);
		} catch (ParserConfigurationException e) {
		}
	}
	
	private void saveDocument(Node document) throws FileNotFoundException {
        try {
        	Transformer transformer = TransformerFactory.newInstance().newTransformer();
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        	DOMSource source=new DOMSource();
        	source.setNode(document);
        	StreamResult result=new StreamResult();
        	result.setOutputStream(new FileOutputStream(file));
			transformer.transform(source, result);
		} catch (TransformerException e) {
		}
	}
		
}
