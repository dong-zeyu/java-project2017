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
	private final String filename = "data.xml";
	private File file;
	private Doc doc;
	private Timer timer;
	
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
						flight.flightStatus = FlightStatus.AVAILABLE;
						flight.setDaemon(false);
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
		timer = new Timer(false);
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

	public FlightDaemon getFlightDaemonByID(int flightID) {
		for (FlightDaemon daemon : flightDaemons) {
			if (daemon.getFlightDaemonID()==flightID) {
				return daemon;
			}
		}
		return null;
	}
	
	private class Doc {
		
		private Document document;
		private Element current;
		
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
				doc.current = doc.appendItem(map, passenger.userID);
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
		//flight
		doc.getIn("flightDaemon").renewElement();
		for (FlightDaemon flight : flightDaemons) {
			HashMap<String, String> map = new HashMap<>();
			map.put("flightName", flight.getFlightName());
			map.put("startTime", String.valueOf(flight.getStartTime().getTime()));
			map.put("arriveTime", String.valueOf(flight.getArriveTime().getTime()));
			map.put("startCity", String.valueOf(flight.getStartCity().getCityID()));
			map.put("arriveCity", String.valueOf((flight.getArriveCity().getCityID())));
			map.put("price", String.valueOf(flight.getPrice()));
			map.put("seatCapacity", String.valueOf(flight.getSeatCapacity()));
			map.put("distance", String.valueOf(flight.getDistance()));
			map.put("period", String.valueOf(flight.getPeriod()));
			map.put("status", String.valueOf(flight.status));
			doc.current = doc.appendItem(map, flight.getFlightDaemonID());
			doc.createElement("flight");
			for (Flight flight2 : flight.children) {
				map = new HashMap<>();
				map.put("flightName", flight2.getFlightName());
				map.put("startTime", String.valueOf(flight2.getStartTime().getTime()));
				map.put("arriveTime", String.valueOf(flight2.getArriveTime().getTime()));
				map.put("startCity", String.valueOf(flight2.getStartCity().getCityID()));
				map.put("arriveCity", String.valueOf((flight2.getArriveCity().getCityID())));
				map.put("price", String.valueOf(flight2.getPrice()));
				map.put("seatCapacity", String.valueOf(flight2.getSeatCapacity()));
				map.put("distance", String.valueOf(flight2.getDistance()));
				map.put("status", flight2.getFlightStatus().name());
				map.put("isDaemon", String.valueOf(flight2.isDaemon()));
				doc.appendItem(map, flight2.getFlightID());
			}
			doc.returnParent().returnParent();
		}
		doc.returnParent();
		//city
		doc.getIn("city").renewElement();
		for (City city : cities) {
			HashMap<String, String> map = new HashMap<>();
			map.put("name", city.getCityName());
			doc.appendItem(map, city.getCityID());
		}
		doc.returnParent();
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
		FlightDaemon.ID = 0;
		try {
			file = new File(filename);
			if (!file.exists()) {	
				file.createNewFile();
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
				FlightDaemon flight1 = new FlightDaemon("A001",
						Flight.calendar(2017, 5, 1, 12, 30, 0),
						Flight.calendar(2017, 5, 1, 14, 40, 0), 24*3600*1000, shenz, beij, 1200, 120, 1800000);
				FlightDaemon flight2 = new FlightDaemon("A002",
						Flight.calendar(2017, 5, 1, 9, 12, 0),
						Flight.calendar(2017, 5, 1, 10, 42, 0), 7*24*3600*1000, beij, shenz, 1200, 120, 1800000);
				FlightDaemon flight3 = new FlightDaemon("A003",
						Flight.calendar(2017, 5, 1, 16, 12, 00),
						Flight.calendar(2017, 5, 1, 16, 52, 00), 24*3600*1000, zhenz, shenz, 1200, 120, 1200000);
				FlightDaemon flight4 = new FlightDaemon("A004",
						Flight.calendar(2017, 5, 1, 10, 55, 00), 
						Flight.calendar(2017, 5, 1, 14, 32, 00), 7*24*3600*1000, shenz, zhenz, 1200, 120, 1200000);
				FlightDaemon flight6 = new FlightDaemon("A006",
						Flight.calendar(2017, 5, 1, 22, 46, 00), 
						Flight.calendar(2017, 5, 2, 00, 10, 00), 24*3600*1000, zhenz, Nanc, 250, 300, 950000);
				FlightDaemon flight7 = new FlightDaemon("A007",
						Flight.calendar(2017, 5, 1, 23, 46, 00), 
						Flight.calendar(2017, 5, 2, 00, 10, 00), 24*3600*1000, Wuh, Hangz, 900, 90, 470000);
				FlightDaemon flight9 = new FlightDaemon("A009",
						Flight.calendar(2017, 5, 1, 11, 46, 00), 
						Flight.calendar(2017, 5, 1, 13, 10, 00), 24*3600*1000, shangh, Hangz, 870, 100, 240000);
				FlightDaemon flight10 = new FlightDaemon("A010",
						Flight.calendar(2017, 5, 1, 17, 46, 00), 
						Flight.calendar(2017, 5, 1, 19, 10, 00), 24*3600*1000, shenz, Hangz, 870, 100, 660000);
				FlightDaemon flight11 = new FlightDaemon("A011",
						Flight.calendar(2017, 5, 1, 17, 46, 00), 
						Flight.calendar(2017, 5, 1, 19, 10, 00), 7*24*3600*1000, Hangz, shenz, 900, 100, 660000);
				FlightDaemon flight12 = new FlightDaemon("A012",
						Flight.calendar(2017, 5, 1, 17, 46, 00), 
						Flight.calendar(2017, 5, 1, 19, 30, 00), 24*3600*1000, Hangz, shangh, 1130, 100, 240000);
				FlightDaemon flight14 = new FlightDaemon("A014",
						Flight.calendar(2017, 5, 1, 15, 46, 00), 
						Flight.calendar(2017, 5, 1, 16, 40, 00), 24*3600*1000, shenz, Wuh, 780, 120, 780000);
				FlightDaemon flight15 = new FlightDaemon("A015",
						Flight.calendar(2017, 5, 1, 15, 46, 00), 
						Flight.calendar(2017, 5, 1, 18, 40, 00), 24*3600*1000, Hangz, Wuh, 860, 120, 340000);
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
				// DONE(Zhu) add remain
				doc.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				doc.current = (Element) doc.document.appendChild(doc.document.createElement("root"));
				doc.createElement("user").returnParent()
					.createElement("city").returnParent()
					.createElement("flightDaemon").returnParent();
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
			doc.returnParent().getIn("flightDaemon");
			int MAX_FLIGHT_ID = 0;
			MAX_ID = 0;
			for (Element element : doc.getChildren()) {
				FlightDaemon.ID = Integer.parseInt(element.getAttribute("mid"));
				MAX_ID = FlightDaemon.ID > MAX_ID ? FlightDaemon.ID : MAX_ID;
				FlightDaemon flight;
				flight = new FlightDaemon(
						element.getElementsByTagName("flightName").item(0).getTextContent(),
						new Date(Long.parseLong(element.getElementsByTagName("startTime").item(0).getTextContent())),
						new Date(Long.parseLong(element.getElementsByTagName("arriveTime").item(0).getTextContent())),
						Integer.parseInt(element.getElementsByTagName("period").item(0).getTextContent()),
						getCityByID(Integer.parseInt(element.getElementsByTagName("startCity").item(0).getTextContent())),
						getCityByID(Integer.parseInt(element.getElementsByTagName("arriveCity").item(0).getTextContent())),
						Integer.parseInt(element.getElementsByTagName("price").item(0).getTextContent()),
						Integer.parseInt(element.getElementsByTagName("seatCapacity").item(0).getTextContent()),
						Integer.parseInt(element.getElementsByTagName("distance").item(0).getTextContent()));
				flight.status = element.getElementsByTagName("status").item(0).getTextContent().equals("true") ? true : false;
				doc.current = element;
				doc.getIn("flight");
				for (Element flight1 : doc.getChildren()) {
					Flight.ID = Integer.parseInt(flight1.getAttribute("mid"));
					MAX_FLIGHT_ID = Flight.ID > MAX_FLIGHT_ID ? Flight.ID : MAX_FLIGHT_ID;
					Flight flight2 = new Flight(
							flight1.getElementsByTagName("flightName").item(0).getTextContent(),
							new Date(Long.parseLong(flight1.getElementsByTagName("startTime").item(0).getTextContent())),
							new Date(Long.parseLong(flight1.getElementsByTagName("arriveTime").item(0).getTextContent())),
							getCityByID(Integer.parseInt(flight1.getElementsByTagName("startCity").item(0).getTextContent())),
							getCityByID(Integer.parseInt(flight1.getElementsByTagName("arriveCity").item(0).getTextContent())),
							Integer.parseInt(flight1.getElementsByTagName("price").item(0).getTextContent()),
							Integer.parseInt(flight1.getElementsByTagName("seatCapacity").item(0).getTextContent()),
							Integer.parseInt(flight1.getElementsByTagName("distance").item(0).getTextContent()));
					flight2.setDaemon(flight1.getElementsByTagName("isDaemon").item(0).getTextContent().equals("true") ? true : false);
					flight2.flightStatus = FlightStatus.valueOf(flight1.getElementsByTagName("status").item(0).getTextContent());
					flights.add(flight2);
					flight.children.add(flight2);
				}
				doc.returnParent().returnParent();
				flightDaemons.add(flight);
			}
			FlightDaemon.ID = MAX_ID + 1;
			Flight.ID = MAX_FLIGHT_ID + 1;
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
									Integer.parseInt(o.getElementsByTagName("seat").item(0).getTextContent().equals("null") ? "-1" : 
										o.getElementsByTagName("seat").item(0).getTextContent()));
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
