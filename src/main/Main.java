package main;

import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import data.City;
import data.Flight;
import data.FlightDaemon;
import data.User;
import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;

/**
 * This is the main class of flight system project designed by group No.25
 * 
 * <p>The feature of this flight system:
 * <ul>
 * 		<li>containing all of the functions required</li>
 * 		<li>using xml file to save data</li>
 * 		<li>saving data to file and changing flight status automatically</li>
 * 		<li>able to search by city and search by date</li>
 * 		<li>add flight automatically</li>
 * 		<li>having high extensibility</li>
 * 		<li>using custom exception to deal with status and permission problem</li>
 * </ul>
 * 
 * <p>This project has a complete git repository, and you can trace the develop procedure if you like.
 *
 */
public class Main {
	static {
		printHelp(true);
		server = new MainServer();
		scanner = new Scanner(System.in);
	}

	static MainServer server;
	static Scanner scanner;

	public static void main(String[] args) {
		// DONE(Dong) UI design
		String string = "";
		String[] param;
		while (!(string.equals("exit") || string.equals("e"))) {
			System.out.print(">");
			string = scanner.nextLine();
			string = string.replaceAll("\\s+", " ");
			string = string.replaceAll("^\\s+", "");
			string = string.replaceAll("\\s+$", "");
			if (string.contains(" ")) {
				String[] cmd = string.split(" ");
				string = cmd[0];
				param = new String[cmd.length - 1];
				for (int i = 1; i < cmd.length; i++) {
					param[i - 1] = cmd[i]; 
				}
			} else {
				param = null;
			}
			switch (string) {
			case "help":
			case "h":
				printHelp(false);
				break;
			case "exit":
			case "e":
				break;
			case "list":
			case "l":
				list(param);
				break;
			case "login":
			case "log":
				login(param);
				break;
			case "register":
			case "r":
				register();
				break;
			case "search":
			case "s":
				if (param == null || param.length == 0) {
					search();
				} else {
					server.search(param[0]);
				}
				break;
			case "add":
				add(param);
				break;
			case "delete":
			case "d":
				delete(param);
				break;
			case "reserve":
			case "re":
				reserve(param);
				break;
			case "unsubscribe":
			case "unsub":
				unsubscribe();
				break;
			case "pay":
				pay();
				break;
			case "publish":
			case "pub":
				pub(param);
				break;
			case "change":
				change(param);
				break;
			default:
				if (!string.equals("")) {
					System.out.println("Unknown command: Type 'help' for more information.");
				}
				break;
			}
		}
		scanner.close();
		server.stop();
	}
	
	/*
	 * These are subUI or a wizard to lead User to do specific work
	 */
	private static void change(String[] param) {
		try {
			switch (param[0]) {
			case "flight":
				try {
					changeFlight(Integer.valueOf(param[1]));
				} catch (NumberFormatException e) {
					System.out.printf("'%s' is not a flight ID\n", param[1]);
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "city":
				try {
					City city = server.getCity(Integer.valueOf(param[1]));
					if (city != null) {
						city.setCityName(param[2]);
						System.out.println("Succeed!");
					} else {
						System.out.println("Failed: no such city");
					}
				} catch (NumberFormatException e) {
					System.out.printf("'%s' is not a city ID\n", param[0]);
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "username":
				try {
					User user = server.getCurrentUser();
					user.setUserName(param[1]);
					System.out.println("Succeed!");
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "password":
				try {
					User user = server.getCurrentUser();
					user.changePass(param[1]);
					System.out.println("Succeed!");
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			default:
				System.out.println("Please input what to change");
				break;
			}
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			System.out.println("Format error: type 'help' for more information");
		}		
	}

	private static void pay() {
		try {
			System.out.print("Please input your password: ");
			if (!server.checkPass(scanner.nextLine())) {
				throw new PermissionDeniedException("Password Error");
			}
			server.displayOrder();
			do {
				System.out.print("please select the index of order to pay(-1 to exit): ");
				try {
					int index = Integer.valueOf(scanner.nextLine());
					if (index == -1) {
						break;
					}
					System.out.println("Are you sure to pay this order?");
					if (scanner.nextLine().toLowerCase().equals("y")) {
						server.pay(index);
						System.out.println("Succeed!");						
					} else {
						System.out.println("Cancled");
					}
				} catch (NumberFormatException e) {
					System.out.println("Please input the right index");
				} catch (StatusUnavailableException e) {
					System.out.println("Pay failed: " + e.getMessage());
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Error: no such order");
				}
			} while (true);
		} catch (PermissionDeniedException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void unsubscribe() {
		try {
			System.out.print("Please Input your password: ");
			if (!server.checkPass(scanner.nextLine())) {
				throw new PermissionDeniedException("Password Error");
			}
			server.displayOrder();
			do {
				System.out.print("please select the index of order to cancel(-1 to exit): ");
				try {
					int index = Integer.valueOf(scanner.nextLine());
					if (index == -1) {
						break;
					}
					System.out.println("Are you sure to cancel this order?");
					if (scanner.nextLine().toLowerCase().equals("y")) {
						if (server.cancel(index)) {
							System.out.println("Reserving money has returned");
						}
						System.out.println("Succeed!");	
						
					} else {
						System.out.println("Operator canceld");
					}
				} catch (NumberFormatException e) {
					System.out.println("Please input the right index");
				} catch (StatusUnavailableException e) {
					System.out.println("Cancel failed: " + e.getMessage());
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Error: no such order");
				}
			} while (true);
		} catch (PermissionDeniedException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void list(String[] param) {
		if (param != null && param.length >= 1) {
			try {
				switch (param[0]) {
				case "city":
					if (param.length == 1) {
						server.displayCity();
					} else {
						for (int i = 1; i < param.length; i++) {
							server.displayCity(Integer.valueOf(param[i]));
						}
					}
					break;
				case "flight":
					if (param.length == 1) {
						server.displayFlight();						
					} else {
						for (int i = 1; i < param.length; i++) {
							try {
								server.displayFlight(Integer.valueOf(param[i]));
							} catch (NumberFormatException e) {
								System.out.printf("'%s' is not a flight\n", param[i]);
							}								
						}
					}						
					break;
				case "daemon":
					server.displayDaemon();						
					break;
				case "user":
					if (param.length == 1) {
						try {
							server.dispalyUser();
						} catch (PermissionDeniedException e) {
							System.out.println(e.getMessage());
						}							
					} else {
						for (int i = 1; i < param.length; i++) {
							try {
								if (!server.dispalyUser(Integer.valueOf(param[i]))) {
									System.out.printf("Can't find user with id '%s'\n", param[i]);
								}
							} catch (NumberFormatException e) {
								System.out.printf("'%s' is not a user id.\n", param[i]);
							}
						}
					}
					break;
				case "order":
					System.out.print("Please Input your password: ");
					if (!server.checkPass(scanner.nextLine())) {
						throw new PermissionDeniedException("Password Error");
					}
					server.displayOrder();
					break;
				default:
					System.out.println("Format error: you can only list city, user, flight or order");
					break;
				}
			} catch (PermissionDeniedException e) {
				System.out.println(e.getMessage());
			}								
		} else {
			System.out.println("Format error: please input what to list");
		}
	}

	private static void pub(String[] param) {
		if (param != null && param.length >= 1) {
			for (String p : param) {
				try {
					Flight flight = server.getFlight(Integer.valueOf(p));
					if (flight != null) {
						flight.publish();
					} else {
						System.out.printf("can't find flight with id '%s'\n", p);
					}
				} catch (NumberFormatException e) {
					System.out.printf("'%s' is not a flight ID\n", p);
				} catch (StatusUnavailableException | PermissionDeniedException e) {
					System.out.printf("cannot publish flight with id '%s': %s\n", p, e.getMessage());
				}
			} 
		} else {
			System.out.println("Format error: use 'publish [ID1] [ID2] ...' to publish flihgts");
		}
	}

	private static void login(String[] param) {
		if (param != null && param.length == 2) {
			if (server.login(param[0], param[1])) {
				System.out.print("Login succeed: ");
				if (server.isAdmin()) {
					System.out.println("You are administrator");
				} else {
					System.out.println("You are passenger");
				}
			} else {
				System.out.println("Login failed");
			}
		} else {
			System.out.println("Format error: please use 'login [username] [password]' to login");
		}
	}

	private static void changeFlight(int flightID) throws PermissionDeniedException {
		FlightDaemon flight = server.getDaemon(flightID);
		if (flight == null) {
			System.out.printf("Cannot find flight daemon with ID '%d'\n", flightID);
			return;
		} else if (!flight.getStatus()) {
			System.out.println("This flight has deleted");
			return;
		}
		System.out.println("ID\tName\tStartCity\tArriveCity\tBeginTime\t\t\tTime\tPeriod\tPrice\tSeatCapacity");
		System.out.println(server.getDaemon(flightID));
		System.out.print("Usage: "
				+ "\tname=newname\n"
				+ "\tstarttime=yyyy-mm-dd-hr-mim-sec\n"
				+ "\tarrivetime=yyyy-mm-dd-hr-mim-sec\n"
				+ "\tstartcity=cityID\n"
				+ "\tarrivecity=cityID\n"
				+ "\tprice=newprice\n"
				+ "\tcapacity=newcapacity\n"
				+ "\tdistance=newdistance\n"
				+ "\texit|e\n"
				+ "Available City: \n");
		server.displayCity();
		String[] input;
		do {
			System.out.print("Please input what to change: ");
			input = scanner.nextLine().replace(" ", "").split("=");
			try {	
				switch (input[0]) {
				case "name":
					flight.setFlightName(input[1]);
					System.out.println("Succeed!");
					break;
				case "starttime":
					String[] sdate = input[1].split("-");
					flight.setStartTime(Flight.calendar(
							Integer.valueOf(sdate[0]), 
							Integer.valueOf(sdate[1]), 
							Integer.valueOf(sdate[2]), 
							Integer.valueOf(sdate[3]), 
							Integer.valueOf(sdate[4]),
							Integer.valueOf(sdate[5])));
					System.out.println("Succeed!");
					break;
				case "arrivetime":
					String[] adate = input[1].split("-");
					flight.setArriveTime(Flight.calendar(
							Integer.valueOf(adate[0]), 
							Integer.valueOf(adate[1]), 
							Integer.valueOf(adate[2]), 
							Integer.valueOf(adate[3]), 
							Integer.valueOf(adate[4]),
							Integer.valueOf(adate[5])));
					System.out.println("Succeed!");
					break;
				case "startcity":
					flight.setStartCity(server.getCity(Integer.valueOf(input[1])));
					System.out.println("Succeed!");
					break;
				case "arrivecity":
					flight.setArriveCity(server.getCity(Integer.valueOf(input[1])));
					System.out.println("Succeed!");
					break;
				case "price":
					flight.setPrice(Integer.valueOf(input[1]));
					System.out.println("Succeed!");
					break;
				case "capacity":
					flight.setSeatCapacity(Integer.valueOf(input[1]));
					System.out.println("Succeed!");
					break;
				case "distance":
					flight.setDistance(Integer.valueOf(input[1]));
					System.out.println("Succeed!");
					break;
				case "exit":
				case "e":
					break;
				default:
					System.out.println("Command error");
					break;
				}
			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				System.out.println("Format error");
			}
		} while (!(input[0].equals("e") || input[0].equals("exit")));
	}

	private static void reserve(String[] param) {
		if (param != null && param.length >= 1) {
			for (String para : param) {
				try {
					if (server.reserveFlight(Integer.parseInt(para))) {
						System.out.println("succeed in " + para);
					} else {
						System.out.println("no flight with id " + para);
					}
				} catch (NumberFormatException e) {
					System.out.printf("'%s' is not a flight id\n", para);
				} catch (PermissionDeniedException | StatusUnavailableException e) {
					System.out.printf("cannot reserve filght with id '%s': %s\n",para, e.getMessage());
				}
			}
		}
	}

	private static void add(String[] param) {
		// DONE(Dong) add
		if (param != null && param.length > 0) {
			switch (param[0]) {
			case "city":
				try {
					addCity(param[1]);
				} catch (IndexOutOfBoundsException e) {
					addCity(null);
				}
				break;
			case "flight":
				addFlight();
				break;
			case "admin":
				addAdmin();
				break;
			default:
				System.out.println("You can only add a city, flight or admin");
				break;
			}
		} else {
			System.out.println("Format error: please use 'add (city|flight|admin)' or 'add city [cityname]' to add");
		}
	}

	private static void delete(String[] param) {
		// DONE(Dong) delete
		if (param != null && param.length >= 2) {
			switch (param[0]) {
			case "flight":
				try {
					for (int i = 1; i < param.length; i++) {
						try {
							if (server.deleteFlight(Integer.parseInt(param[i]))) {
								System.out.printf("Successfully delete flight '%s'!\n", param[i]);
							} else {
								System.out.printf("Delete flight '%s' failed: no such flight\n", param[i]);
							}
						} catch (NumberFormatException e) {
							System.out.printf("'%s' is not a flight id!\n", param[i]);
						} catch (StatusUnavailableException e) {
							System.out.printf("Delete flight '%s' failed: %s\n", param[i], e.getMessage());
						}
					}
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "daemon":
				try {
					for (int i = 1; i < param.length; i++) {
						try {
							if (server.deleteFlightDaemon(Integer.parseInt(param[i]))) {
								System.out.printf("Successfully delete flight daemon '%s'!\n", param[i]);
							} else {
								System.out.printf("Delete flight daemon '%s' failed: no such flight daemon\n", param[i]);
							}
						} catch (NumberFormatException e) {
							System.out.printf("'%s' is not a flight daemon id!\n", param[i]);
						} catch (StatusUnavailableException e) {
							System.out.printf("Delete flight daemon '%s' failed: %s\n", param[i], e.getMessage());
						}
					}
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "city":
				try {
					for (int i = 1; i < param.length; i++) {
						try {
							if (server.deleteCity(Integer.parseInt(param[i]))) {
								System.out.printf("Successfully delete city '%s'!\n", param[i]);
							} else {
								System.out.printf("Delete city '%s' failed: no such city\n", param[i]);
							}
						} catch (NumberFormatException e) {
							System.out.printf("'%s' is not a city id!\n", param[i]);
						} catch (StatusUnavailableException e) {
							System.out.printf("Delete city '%s' failed: %s\n", param[i], e.getMessage());
						}
					}
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "user":
				try {
					for (int i = 1; i < param.length; i++) {
						try {
							if (server.deleteUser(Integer.parseInt(param[i]))) {
								System.out.printf("Successfully delete user '%s'!\n", param[i]);
							} else {
								System.out.printf("Delete user '%s' failed: no such user\n", param[i]);
							}
						} catch (NumberFormatException e) {
							System.out.printf("'%s' is not a user id!\n", param[i]);
						}
					}
				} catch (PermissionDeniedException e) {
					System.out.println(e.getMessage());
				}
				break;
			default:
				System.out.println("You can only delete a city, flight or user");
				break;
			}
		} else {
			System.out.println("Format error: please use 'delete (city|flight|user) [ID1] [ID2] ...' to delete");
		}
	}

	private static void search() {
		// DONE(Dong) search
		String cmd = "";
		int cityFromId = -1;
		int cityToId = -1;
		Date date1 = null;
		Date date2 = null;
		System.out.print("welcome to search page! you can input: \n"
				+ "\tcity CityFromId-CityToId\n"
				+ "\t\tset filter of city\n"
				+ "\tcity -\n"
				+ "\t\tclear the filter of city\n"
				+ "\tdate ( yyyy-mm-dd~yyyy-mm-dd | ~yyyy-mm-dd | yyyy-mm-dd~ | ~ )\n"
				+ "\t\tset filter of 'set off date' interval\n"
				+ "\tprint|p\tprint result using filter\n"
				+ "\texit|e\texit wizard\n\n"
				+ "\tavailibal city: \n");
		server.displayCity();
		do {
			System.out.print("current filter: \n"
					+ String.format("\tcity: %s-%s\n", cityFromId == -1 ? "unset" : String.valueOf(cityFromId), cityToId == -1 ? "unset" : String.valueOf(cityToId))
					+ String.format("\tdate: %s~%s\n\n", date1 == null ? "unset" : date1.toString(), date2 == null ? "unset" : date2.toString())
					+ "use 'print' or 'p' to print result\n");
			System.out.print(">>");
			String param;
			String input = scanner.nextLine();
			input = input.replaceAll("\\s+", " ");
			input = input.replaceAll("^\\s+", "");
			input = input.replaceAll("\\s+$", "");
			if (input.contains(" ")) {
				cmd = input.split(" ")[0];
				param = input.split(" ")[1];
			} else {
				cmd = input;
				param = "";
			}
			switch (cmd) {
			case "city":
				try {
					String[] cityid = param.split("-");
					cityFromId = param.equals("-") ? -1 :
						param.startsWith("-") ? -1 :
							Integer.valueOf(cityid[0]);
					cityToId = param.equals("-") ? -1 :
						param.endsWith("-") ? -1 :
							Integer.valueOf(cityid[1]);
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					System.out.println("City ID format error");
				}
				break;
			case "date":
				try {
					String[] s0 = null;
					String[] s1 = null;
					if (param.split("~").length >= 1) {
						s0 = param.split("~")[0].split("-");						
					} 
					if (param.split("~").length == 2) {
						s1 = param.split("~")[1].split("-");
					}
					date1 = param.equals("~") ? null : 
						param.startsWith("~") ? null :
							Flight.calendar(
									Integer.valueOf(s0[0]),
									Integer.valueOf(s0[1]),
									Integer.valueOf(s0[2]), 0, 0, 0);
					date2 = param.equals("~") ? null : 
						param.endsWith("~") ? null : 
							Flight.calendar(
									Integer.valueOf(s1[0]),
									Integer.valueOf(s1[1]),
									Integer.valueOf(s1[2]), 0, 0, 0);
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					System.out.println("Date format error");
				}
				break;
			case "exit":
			case "e":
				break;
			case "print":
			case "p":
				server.search(cityFromId, cityToId, date1, date2);
				break;
			default:
				System.out.println("unknown command");
				break;
			}
		} while (!(cmd.equals("exit") || cmd.equals("e")));
	}
	
	private static void addAdmin() {
		// DONE(Peng) addAdmin UI
		System.out.print("Please enter the Username : ");
		String userName=scanner.nextLine();
		System.out.print("Please enter the password : ");
		String password=scanner.nextLine();
		try {
			server.addAdmin(userName, password);
			System.out.println("Added successfully");
		} catch (PermissionDeniedException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void register() {
		// DONE(Zhu) register UI
		System.out.print("Please input your username: ");
		String username;
		username=scanner.nextLine();
		System.out.print("Please input your identity card number: ");
		String idNumber;
		idNumber=scanner.nextLine();
		while(idNumber.length()>18||idNumber.length()<18){
			System.out.print("Please input the correct identity card number: ");
		idNumber=scanner.nextLine();
		}
		String password,password2;
		System.out.print("Please input your password: ");
		password=scanner.nextLine();
		do {
			System.out.print("Please input your password again: ");
		password2=scanner.nextLine();	
		} while (!(password.equals(password2)));		
		server.addPassenger(username, idNumber, password2);
		System.out.println("Succeed in creating your account!");
		
	}

	private static void addFlight() {
		// DONE(Peng) addFlight UI
		try {
			System.out.println("Available City: ");
			server.displayCity();
			System.out.print("flightName： ");
			String flightName=scanner.nextLine();
			System.out.print("Please enter the Starttime,formatted with : year-month-date-hr-min-sec: ");
			String[] startime=scanner.nextLine().split("-");
			int year =Integer.parseInt(startime[0]);
			int month =Integer.parseInt(startime[1]);
			int date =Integer.parseInt(startime[2]);
			int hr =Integer.parseInt(startime[3]);
			int min =Integer.parseInt(startime[4]);
			int sec =Integer.parseInt(startime[5]);
			Date startTime = Flight.calendar(year, month, date, hr, min, sec);
			System.out.print("Please enter the arrivetime,formatted with : year-month-date-hr-min-sec: ");
			String[] arrivetime=scanner.nextLine().split("-");
			int year1 =Integer.parseInt(arrivetime[0]);
			int month1 =Integer.parseInt(arrivetime[1]);
			int date1 =Integer.parseInt(arrivetime[2]);
			int hr1 =Integer.parseInt(arrivetime[3]);
			int min1 =Integer.parseInt(arrivetime[4]);
			int sec1 =Integer.parseInt(arrivetime[5]);
			Date arriveTime = Flight.calendar(year1, month1, date1, hr1, min1, sec1);
			if (arriveTime.before(startTime) || startTime.before(new Date())) {
				throw new NumberFormatException();
			}
			System.out.print("Period of the flight(day)(0 for no period): ");
			int period = scanner.nextInt();
			System.out.print("startCityID: ");
			int startCityID=scanner.nextInt();
			System.out.print("arriveCityID: ");
			int arriveCityID=scanner.nextInt();
			if (startCityID == arriveCityID) {
				throw new NumberFormatException();
			}
			System.out.println("price");
			int price=scanner.nextInt();
			System.out.print("seatCapacity: ");
			int seatCapacity=scanner.nextInt();
			System.out.print("distance(m): ");
			int distance = scanner.nextInt();
			scanner.nextLine();
			if (!server.createFlightDaemon(flightName, startTime, arriveTime, period, startCityID, arriveCityID, price, seatCapacity, distance)) {
				System.out.println("Error in cityID. retry?");
				if (scanner.nextLine().toLowerCase().equals("y")) {
					addFlight();
				}
			} else {
				System.out.print("Flight added successfully\n");
			}
		} catch (PermissionDeniedException e) {
			System.out.println(e.getMessage());
		} catch (IndexOutOfBoundsException | NumberFormatException | InputMismatchException e) {
			System.out.print("Input error. retry?");
			if (scanner.nextLine().toLowerCase().equals("y")) {
				addFlight();				
			}
		}
	}
	
	private static void addCity(String cityname) {
		// DONE(Peng) addCity UI
		if (cityname == null) {
			System.out.print("Please enter a valid city name: ");
			cityname = scanner.nextLine();
		}
		try {
			server.addCity(cityname);
			System.out.println("City added successfully");
		} catch (PermissionDeniedException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void printHelp(boolean isMini) {
		// DONE(Dong) Help
		if (isMini) {
			System.out.print("Welcome to flight system!\n"
					+ "please use 'login [username] [password]' to login or use 'register' to register an account\n"
					+ "type 'help' for more information.\n");
		} else {
			System.out.print("Usage: command [param...]\n"
					+ "Available command: \n\n"
					+ "\tlogin|log [username] [password]\n"
					+ "\t\tlogin with username and password\n\n"
					+ "\tregister\n"
					+ "\t\tregister an account\n\n"
					+ "\tsearch|s [flightName]\n"
					+ "\t\tsearch flight with specific name\n\n"
					+ "\tsearch|s\n"
					+ "\t\tsearch flight with some filter\n\n"
					+ "\tlist|l (city|user|flight) [ID]\n"
					+ "\t\tlist all city, users(only for adminstrator), flight and in the server, or list the element with specific ID in detail\n\n"
					+ "\tlist|l order\n"
					+ "\t\tlist the order\n\n"
					+ "\tlist|l daemon\n"
					+ "\t\tlist flight daemon\n\n"
					+ "\tadd (city|admin|flight)\n"
					+ "\t\tadd a city administrator or flight daemon(only for adminstrator)\n\n"
					+ "\tdelete|d (city|user|flight|daemon) [ID1] [ID2] ....\n"
					+ "\t\tdelete city, user, flight or flight daemon with specific ID(only for adminstrator)\n"
					+ "\t\t\t**caution： delete flight daemon will also delete corresponding flight with status UNPUBLISHED\n\n"
					+ "\treserve|re [ID1] [ID2] ....\n"
					+ "\t\treserve flights with specific ID\n\n"
					+ "\tunsubscribe|unsub\n"
					+ "\t\tgoes into unsubscribe page\n\n"
					+ "\tpay\n"
					+ "\t\tgoes into pay page\n\n"
					+ "\tchange flight [ID]\n"
					+ "\t\tchange flight daemon information with specific ID(only for adminstrator)\n\n"
					+ "\tchange city [ID] [newName]\n"
					+ "\t\tchange city name with specific ID(only for adminstrator)\n\n"
					+ "\tchange (username|password) [newName|newPass]\n"
					+ "\t\tchange username or password\n\n"
					+ "\thelp|h\n"
					+ "\t\tprint this help information\n\n"
					+ "\texit|e\n"
					+ "\t\texit this program\n\n"
					);
		}
	}

}
