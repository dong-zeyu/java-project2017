package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

import exceptions.StatusUnavailableException;

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
	
	// DONE(Dong) This class is all of my job :(
	public ArrayList<User> users;
	public ArrayList<Flight> flights;
	public ArrayList<City> cities;
	public ArrayList<FlightDaemon> flightDaemons;
	public static final long SYNC_INTERVAL = 20*1000 + 53; // 20 second
	public static final long CHECKING_INTERVAL = 1000l; // 1 second
	public static final long DAY_OF_CREATE = 18*24*3600*1000l; // 18 days
	public static final long INTERVAL_TO_CREATE = 24*3600*1000l; // 1 day
	public static final long TIME_TO_TERMINATE = 2*3600*1000l; // 2 hours
	private final String filename = "data.xml";
	private File file;
	private Doc doc;
	private int usersHash;
	private int flightsHash;
	private int citiesHash;
	private Timer timer;
	
	class SaveFileTask extends TimerTask {
		
		@Override
		public synchronized void run() {
			try {
				saveData();
			} catch (FileNotFoundException e) {
				try {
					init();
				} catch (IOException e1) {
					System.err.println("Write data error!");
					System.exit(-1);
				}
			}
		}
		
	}
	
	class ChangeFlight extends TimerTask {

		@Override
		public void run() {
			for (Flight flight : flights) {
				if (flight.getStartTime().getTime() - new Date().getTime() <= TIME_TO_TERMINATE) {
					flight.flightStatus = FlightStatus.TERMINATE;
				} else  {
					if (flight.getNumber() == flight.getSeatCapacity()) {
						flight.flightStatus = FlightStatus.FULL;
					} else {
						flight.flightStatus = FlightStatus.AVAILABLE;
					}
				}
			}
		}
		
	}
	
	class CreateFlight extends TimerTask {

		@Override
		public void run() {
			for (FlightDaemon flightDaemon : flightDaemons) {
				long now = new Date().getTime();
				long end = now + DAY_OF_CREATE;
				if (end < flightDaemon.getStartTime().getTime()) {
					continue;
				} else {
					
				}
				for (long i = flightDaemon.getStartTime().getTime(); i < end; i+=flightDaemon.getPeriod()) {
					boolean isCreate = false;
					for (Flight flight : flightDaemon.children) {
						if (!(flight.getStartTime().getTime() == i && flight.getFlightName() == flightDaemon.getFlightName())) {
							isCreate = true;
							break;
						}
					}
					if (isCreate) {
						flights.add(new Flight(
								flightDaemon.getFlightName(), 
								new Date(i), 
								new Date(i + flightDaemon.getArriveTime().getTime() - flightDaemon.getArriveTime().getTime()), 
								flightDaemon.getStartCity(), 
								flightDaemon.getArriveCity(), 
								flightDaemon.getPrice(), 
								flightDaemon.getSeatCapacity(), 
								flightDaemon.getDistance()));
					}
				}
			}
		}
		
	}
	
	public void stop() {
		timer.cancel();
		try {
			saveData();
		} catch (FileNotFoundException e) {
			System.out.println("Saving data faild, continue to stop...");
		}
	}
	
	public DataManager() {
		try {
			init();
		} catch (IOException e) {
			System.err.println("Read/write data error!");
			System.exit(-1);
		}
		usersHash = users.hashCode();
		flightsHash = flights.hashCode();
		citiesHash =  cities.hashCode();
		timer = new Timer(false);
		timer.schedule(new SaveFileTask(), SYNC_INTERVAL, SYNC_INTERVAL);
		timer.schedule(new ChangeFlight(), CHECKING_INTERVAL, CHECKING_INTERVAL);
		timer.schedule(new CreateFlight(), 0, INTERVAL_TO_CREATE);
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
	
	private class Doc {
		
		private Document document;
		private Element current;
		
		public void setCurrent(Element current) {
			this.current = current;
		}
		
		public Doc getIn(String tag) {
			return getIn(tag, 0);
		}
		
		public Doc getIn(String tag, int index) {
			NodeList nodes = current.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE && nodes.item(i).getNodeName().equals(tag)) {
					if (index != 0) {
						index--;
					} else {
						current = (Element) nodes.item(i);
					}
				}
			}
			return this;
		}
		
		public ArrayList<Element> getChildren() {
			ArrayList<Element> elements = new ArrayList<>();
			NodeList list = current.getChildNodes();
			for(int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
					elements.add((Element) list.item(i));
				}
			}
			return elements;
		}
		
		public Doc returnParent() {
			current = (Element) current.getParentNode();
			return this;
		}
		
		public Doc createElement(String tag) {
			current = (Element) current.appendChild(document.createElement(tag));
			return this;
		}
		
		public Doc renewElement() {
			Element element = (Element) document.createElement(current.getTagName());
			current.getParentNode().replaceChild(element, current);
			current = element;
			return this;
		}
		
		public Element appendItem(Map<String, String> map, int id) {
			Element item = (Element) current.appendChild(document.createElement("item"));
			item.setAttribute("mid", String.valueOf(id));
			current.getFirstChild();
			current = item;
			appendChirldren(map);
			current = (Element) current.getParentNode();
			return item;
		}
		
		public Element appendItem(Map<String, String> map) {
			Element item = (Element) current.appendChild(document.createElement("item"));
			current = item;
			appendChirldren(map);
			current = (Element) current.getParentNode();
			return item;
		}
		
		public Doc appendChirldren(Map<String, String> map) {
			for (String string : map.keySet()) {
				current.appendChild(document.createElement(string)).appendChild(document.createTextNode(map.get(string)));
			}
			return this;
		}
		
		public void saveDocument() throws FileNotFoundException {
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
	
	public void saveData() throws FileNotFoundException {
		if (usersHash != users.hashCode()) {
			usersHash = users.hashCode();
			//Users
			doc.getIn("user").renewElement();
			for (User user : users) {
				HashMap<String, String> map = new HashMap<>();
				if (user instanceof Admin) {
					map.put("userName", user.getUserName());
					map.put("passHash", user.getPassHash());
					doc.appendItem(map, user.userID).setAttribute("isAdmin", "true");
				} else {
					Passenger passenger = (Passenger) user;
					map.put("userName", passenger.getUserName());
					map.put("passHash", passenger.getPassHash());
					map.put("identityID", passenger.getIdentityID());
					map.put("userName", passenger.getUserName());
					doc.setCurrent(doc.appendItem(map, passenger.userID));
					doc.current.setAttribute("isAdmin", "false");
					doc.createElement("order");
					for (Order order : passenger.orderList) {
						map = new HashMap<>();
						map.put("flightID", String.valueOf(order.getFlight().getFlightID()));
						map.put("seat", String.valueOf(order.getSeat()));
						map.put("date", String.valueOf(order.getCreatDate().getTime()));
						map.put("status", String.valueOf(order.getStatus()));
						doc.appendItem(map);
					}
					doc.returnParent().returnParent();
				}
			}
			doc.returnParent();
		}
		if (flightsHash != flights.hashCode()) {
			flightsHash = flights.hashCode();
			//flight
			doc.getIn("flight").renewElement();
			for (Flight flight : flights) {
				HashMap<String, String> map = new HashMap<>();
				map.put("flightName", flight.getFlightName());
				map.put("startTime", String.valueOf(flight.getStartTime().getTime()));
				map.put("arriveTime", String.valueOf(flight.getArriveTime().getTime()));
				map.put("startCity", String.valueOf(flight.getStartCity().getCityID()));
				map.put("arriveCity", String.valueOf((flight.getArriveCity().getCityID())));
				map.put("price", String.valueOf(flight.getPrice()));
				map.put("seatCapacity", String.valueOf(flight.getSeatCapacity()));
				map.put("distance", String.valueOf(flight.getDistance()));
				map.put("status", flight.getFlightStatus().name());
				doc.appendItem(map, flight.getFlightID());
			}
			doc.returnParent();
		}
		if (citiesHash != cities.hashCode()) {
			citiesHash = cities.hashCode();
			//city
			doc.getIn("city").renewElement();
			for (City city : cities) {
				HashMap<String, String> map = new HashMap<>();
				map.put("name", city.getCityName());
				doc.appendItem(map, city.getCityID());
			}
			doc.returnParent();
		}
		doc.saveDocument();
	}

	private void init() throws IOException {
		flights = new ArrayList<>();
		users = new ArrayList<>();
		cities = new ArrayList<>();
		flightDaemons = new ArrayList<>();
		doc = new Doc();
		User.ID = 0;
		Flight.ID = 0;
		City.ID = 0;
		try {
			file = new File(filename);
			if (!file.exists()) {	
				file.createNewFile();
				Admin admin = new Admin("Admin", "admin");
				users.add(admin);
				City shenz = new City("Shenzhen");
				City beij = new City("Beijing");
				City zhenz = new City("Zhengzhou");
				City guangz = new City("Guangzhou");
				City shangh = new City("Shanghai");
				City xia = new City("Xi'an");
				City Wuh = new City("Wuhan");
				City Nanc = new City("Nanchang");
				City Hangz = new City("Hangzhou");
				cities.add(zhenz);
				cities.add(beij);
				cities.add(shenz);
				cities.add(guangz);
				cities.add(shangh);
				cities.add(xia);
				cities.add(Wuh);
				cities.add(Nanc);
				cities.add(Hangz);
				Flight flight1 = new Flight("A001",
						Flight.calendar(2017, 4, 1, 9, 30, 0),
						Flight.calendar(2017, 4, 1, 10, 40, 0), shenz, beij, 1200, 120, 1800000);
				Flight flight2 = new Flight("A002",
						Flight.calendar(2017, 5, 2, 9, 12, 0),
						Flight.calendar(2017, 5, 2, 10, 42, 0), beij, shenz, 1200, 120, 1800000);
				Flight flight3 = new Flight("A003",
						Flight.calendar(2017, 3, 3, 16, 12, 00),
						Flight.calendar(2017, 3, 3, 16, 52, 00), zhenz, shenz, 1200, 120, 1200000);
				Flight flight4 = new Flight("A004",
						Flight.calendar(2017, 6, 8, 10, 55, 00), 
						Flight.calendar(2017, 6, 8, 14, 32, 00), shenz, zhenz, 1200, 120, 1200000);
				Flight flight5 = new Flight("A005",
						Flight.calendar(2017, 8, 8, 10, 31, 00), 
						Flight.calendar(2017, 8, 8, 12, 32, 00), guangz, zhenz, 10000, 10, 120000);
				Flight flight6 = new Flight("A006",
						Flight.calendar(2017, 9, 1, 22, 46, 00), 
						Flight.calendar(2017, 9, 2, 00, 10, 00), zhenz, Nanc, 250, 300, 950000);
				Flight flight7 = new Flight("A007",
						Flight.calendar(2017, 12, 30, 23, 46, 00), 
						Flight.calendar(2018, 1, 1, 02, 10, 00), Wuh, Hangz, 900, 90, 470000);
				Flight flight8 = new Flight("A008",
						Flight.calendar(2017, 6, 30, 10, 46, 00), 
						Flight.calendar(2017, 6, 30, 13, 10, 00), xia, Hangz, 900, 90, 840000);
				Flight flight9 = new Flight("A009",
						Flight.calendar(2017, 2, 3, 11, 46, 00), 
						Flight.calendar(2017, 2, 3, 13, 10, 00), shangh, Hangz, 870, 100, 240000);
				Flight flight10 = new Flight("A010",
						Flight.calendar(2017, 3, 10, 17, 46, 00), 
						Flight.calendar(2017, 3, 10, 19, 10, 00), shenz, Hangz, 870, 100, 660000);
				Flight flight11 = new Flight("A011",
						Flight.calendar(2017, 4, 10, 17, 46, 00), 
						Flight.calendar(2017, 4, 10, 19, 10, 00), Hangz, shenz, 900, 100, 660000);
				Flight flight12 = new Flight("A012",
						Flight.calendar(2017, 5, 23, 17, 46, 00), 
						Flight.calendar(2017, 5, 23, 19, 30, 00), Hangz, shangh, 1130, 100, 240000);
				Flight flight13 = new Flight("A013",
						Flight.calendar(2017, 9, 25, 15, 46, 00), 
						Flight.calendar(2017, 9, 25, 17, 40, 00), Hangz, xia, 860, 120, 1000000);
				Flight flight14 = new Flight("A014",
						Flight.calendar(2017, 11, 25, 15, 46, 00), 
						Flight.calendar(2017, 11, 25, 16, 40, 00), shenz, Wuh, 780, 120, 780000);
				Flight flight15 = new Flight("A015",
						Flight.calendar(2017, 12, 19, 15, 46, 00), 
						Flight.calendar(2017, 12, 19, 18, 40, 00), Hangz, Wuh, 860, 120, 340000);
				Flight flight16 = new Flight("A016",
						Flight.calendar(2017, 7, 16, 15, 46, 00), 
						Flight.calendar(2017, 7, 16, 18, 40, 00), zhenz, xia, 900, 120, 1400000);
				Flight flight17 = new Flight("A017",
						Flight.calendar(2017, 9, 5, 7, 46, 00), 
						Flight.calendar(2017, 9, 5, 19, 40, 00), xia, zhenz, 1200, 120, 1400000);
				Flight flight18 = new Flight("A018",
						Flight.calendar(2017, 9, 5, 7, 46, 00), 
						Flight.calendar(2017, 9, 5, 19, 40, 00), shenz, xia, 1200, 120, 1400000);
				flights.add(flight1);
				flights.add(flight2);
				flights.add(flight3);
				flights.add(flight4);
				flights.add(flight5);
				flights.add(flight6);
				flights.add(flight7);
				flights.add(flight8);
				flights.add(flight9);
				flights.add(flight10);
				flights.add(flight11);
				flights.add(flight12);
				flights.add(flight13);
				flights.add(flight14);
				flights.add(flight15);
				flights.add(flight16);
				flights.add(flight17);
				flights.add(flight18);
				// DONE(Zhu) add remain
				doc.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				doc.current = (Element) doc.document.appendChild(doc.document.createElement("root"));
				doc.createElement("user").returnParent()
					.createElement("flight").returnParent()
					.createElement("city").returnParent();
				saveData();
			} else {
				doc.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				doc.current = doc.document.getDocumentElement();
				readData();
			}
		} catch (FileNotFoundException | SAXException e) {
			System.gc();
			file.delete();
			init();
		} catch (ParserConfigurationException e) {
		}
	}

	private void readData() throws SAXException, FileNotFoundException, IOException {
		int MAX_ID;
		try {
			doc.getIn("city");
			MAX_ID = 0;
			for (Element element : doc.getChildren()) {
				City.ID = Integer.parseInt(element.getAttribute("mid"));
				MAX_ID = City.ID > MAX_ID ? City.ID : MAX_ID;
				cities.add(new City(element.getElementsByTagName("name").item(0).getTextContent()));
			}
			City.ID = MAX_ID + 1;
			doc.returnParent().getIn("flight");
			MAX_ID = 0;
			for (Element element : doc.getChildren()) {
				Flight.ID = Integer.parseInt(element.getAttribute("mid"));
				MAX_ID = Flight.ID > MAX_ID ? Flight.ID : MAX_ID;
				Flight flight;
				flight = new Flight(
						element.getElementsByTagName("flightName").item(0).getTextContent(),
						new Date(Long.parseLong(element.getElementsByTagName("startTime").item(0).getTextContent())),
						new Date(Long.parseLong(element.getElementsByTagName("arriveTime").item(0).getTextContent())),
						getCityByID(Integer.parseInt(element.getElementsByTagName("startCity").item(0).getTextContent())),
						getCityByID(Integer.parseInt(element.getElementsByTagName("arriveCity").item(0).getTextContent())),
						Integer.parseInt(element.getElementsByTagName("price").item(0).getTextContent()),
						Integer.parseInt(element.getElementsByTagName("seatCapacity").item(0).getTextContent()),
						Integer.parseInt(element.getElementsByTagName("distance").item(0).getTextContent()));
				flight.flightStatus = FlightStatus.valueOf(element.getElementsByTagName("status").item(0).getTextContent());
				flights.add(flight);	
			}
			Flight.ID = MAX_ID + 1;
			doc.returnParent().getIn("user");
			MAX_ID = 0;
			for (Element element : doc.getChildren()) {
				User.ID = Integer.parseInt(element.getAttribute("mid"));
				MAX_ID = User.ID > MAX_ID ? User.ID : MAX_ID;
				if (element.getAttribute("isAdmin").equals("true")) {
					Admin admin = new Admin(
							element.getElementsByTagName("userName").item(0).getTextContent(),
							null
							);
					admin.passHash = element.getElementsByTagName("passHash").item(0).getTextContent();
					users.add(admin);
				} else if (element.getAttribute("isAdmin").equals("false")) {
					Passenger p = new Passenger(
							element.getElementsByTagName("identityID").item(0).getTextContent(),
							element.getElementsByTagName("userName").item(0).getTextContent(),
							null);
					p.passHash = element.getElementsByTagName("passHash").item(0).getTextContent();
					doc.current = element;
					doc.getIn("order");
					for (Element o : doc.getChildren()) {
						Order order;
						try {
							order = new Order(p,
									getFlightByID(Integer.parseInt(o.getElementsByTagName("flightID").item(0).getTextContent())),
									Integer.parseInt(o.getElementsByTagName("seat").item(0).getTextContent()));
							order.setStatus(OrderStatus.valueOf(o.getElementsByTagName("status").item(0).getTextContent()));
							order.setCreatDate(new Date(Long.parseLong(o.getElementsByTagName("date").item(0).getTextContent())));
							p.addOrder(order);
						} catch (StatusUnavailableException e) {/* ignored */}				
					}
					doc.returnParent().returnParent();
					users.add(p);
				}
			}
			User.ID = MAX_ID + 1;
			doc.returnParent();
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			System.gc();
			file.delete();
			init();
		}
	}
		
}
