package main;

import java.util.Date;
import java.util.Scanner;

import javax.crypto.spec.PBEParameterSpec;

import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;
import flight.Flight;

public class Main {

	static MainServer server = new MainServer();
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		// DONE(Dong) UI design
		String string = "";
		String[] param;
		printHelp(true);
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
				if (param != null) {
					switch (param[0]) {
					case "city":
						if (param.length == 1) {
							System.out.println(server.displayCity());							
						} else {
							for (int i = 1; i < param.length; i++) {
								System.out.println(server.displayCity(Integer.valueOf(param[i])));
							}
						}
						break;
					case "flight":
						if (param.length == 1) {
							System.out.println(server.displayFlight());						
						} else {
							for (int i = 1; i < param.length; i++) {
								System.out.println(server.displayFlight(i));								
							}
						}						
						break;
					case "user":
						if (param.length == 1) {
							System.out.println(server.dispalyUser());							
						} else {
							for (int i = 1; i < param.length; i++) {
								System.out.println(server.dispalyUser(Integer.valueOf(param[i])));								
							}
						}
						break;
					default:
						break;
					}
				}
				break;
			case "login":
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
					System.out.println("Format error");
				}
				break;
			case "register":
			case "r":
				register();
				break;
			case "search":
			case "s":
				search();
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
				break;
			case "publish":
			case "pub":
				if (param != null && param.length >= 1) {
					for (String p : param) {
						try {
							Flight flight = server.getFlight(Integer.valueOf(p));
							if (flight != null) {
								flight.publish();
							} else {
								System.out.printf("can't find flight with id 'p'\n", p);
							}
						} catch (NumberFormatException e) {
							System.out.printf("'%s' is not a flight ID\n", p);
						} catch (PermissionDeniedException e) {
							System.out.println("permission denied");
						} catch (StatusUnavailableException e) {
							System.out.printf("cannot publish flight id '%s' with status %s\n", p, e.getMessage());
						}
					} 
				} else {
					System.out.println("format error");
				}
				break;
			case "change":
				if (param != null && param.length == 1) {
					try {
						changeFlight(Integer.valueOf(param[0]));
					} catch (NumberFormatException e) {
						System.out.printf("'%s' is not a flight ID", param[0]);
					} catch (PermissionDeniedException e) {
						System.out.println("permissiion denied");
					}			
				} else {
					System.out.println("please input the flightID to change");
				}
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
	private static void changeFlight(int flightID) throws PermissionDeniedException {
		Flight flight = server.getFlight(flightID);
		System.out.print("Usage: "
				+ "\tname=newname\n"
				+ "\tstarttime=yyyy-mm-dd-hr-mim-sec\n"
				+ "\tarrivetime=yyyy-mm-dd-hr-mim-sec\n"
				+ "\tstartcity=cityID\n"
				+ "\tarrivecity=cityID\n"
				+ "\tprice=newprice\n"
				+ "\tcapacity=newcapacity\n"
				+ "\texit|e\n");
		String[] input;
		do {
			System.out.print("please input what to change: ");
			input = scanner.nextLine().replace(" ", "").split("=");
			try {
				switch (input[0]) {
				case "name":
					flight.setFlightName(input[1]);
					System.out.println("succeed!");
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
					System.out.println("succeed!");
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
					System.out.println("succeed!");
					break;
				case "startcity":
					flight.setStartCity(server.getCity(Integer.valueOf(input[1])));
					System.out.println("succeed!");
					break;
				case "arrivecity":
					flight.setArriveCity(server.getCity(Integer.valueOf(input[1])));
					System.out.println("succeed!");
					break;
				case "price":
					flight.setPrice(Integer.valueOf(input[1]));
					System.out.println("succeed!");
					break;
				case "capacity":
					flight.setSeatCapacity(Integer.valueOf(input[1]));
					System.out.println("succeed!");
					break;
				case "exit":
				case "e":
					break;
				default:
					System.out.println("command error");
					break;
				}
			} catch (StatusUnavailableException e) {
				System.out.printf("cannot change %s with flight status %s", input[0], e.getMessage());
			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				System.out.println("format error");
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
					System.out.printf("error: '%s' is not a flight id\n", para);
				} catch (PermissionDeniedException e) {
					System.out.println("adminstrator cannot reserve flight");
				} catch (StatusUnavailableException e) {
					System.out.printf("error in reserve filght id '%s' with status %s\n", para, e.getMessage());
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
				System.out.println("You can only add a city, flight or user");
				break;
			}
		} else {
			System.out.println("Format error");
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
						}
					}
				} catch (PermissionDeniedException e) {
					System.out.println("Permission denied: you are not a administrator");
				} catch (StatusUnavailableException e) {
					System.out.println("Can't delete! Status unavailable");
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
							System.out.println(e.getMessage());
						}
					}
				} catch (PermissionDeniedException e) {
					System.out.println("Permission denied: you are not a administrator");
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
						} catch (StatusUnavailableException e) {
							System.out.printf("Cannot delete user in flight that have status %s", e.getMessage());
						}
					}
				} catch (PermissionDeniedException e) {
					System.out.println("Permission denied: you are not a administrator");
				}
				break;
			default:
				System.out.println("You can only delete a city, flight or user");
				break;
			}
		} else {
			System.out.println("Format error");
		}
	}

	private static void search() {
		// TODO(Dong) search
		String cmd = "";
		int cityFromId = -1;
		int cityToId = -1;
		Date date1 = null;
		Date date2 = null;
		System.out.print("welcome to search wizard! you can input: \n"
				+ "\tcity CityFromId-CityToId | - (clear) \n"
				+ "\t\tset filter of city\n"
				+ "\tdate yyyy-mm-dd~yyyy-mm-dd | ~yyyy-mm-dd | yyyy-mm-dd~\n"
				+ "\t\tset filter of 'set off date' interval\n"
				+ "\tprint\tprint result using filter\n"
				+ "\texit|e\texit wizard\n\n"
				+ "\tavailibal city: \n"
				+ server.displayCity() + "\n\n");
		do {
			System.out.print("current filter: \n"
					+ String.format("\tcity: %s-%s\n", cityFromId == -1 ? "unset" : String.valueOf(cityFromId), cityToId == -1 ? "unset" : String.valueOf(cityToId))
					+ String.format("\tdate: %s~%s\n\n", date1 == null ? "unset" : date1.toString(), date2 == null ? "unset" : date2.toString())
					+ "use 'print' to print result\n");
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
					System.out.println("city ID format error");
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
					System.out.println("date format error");
				}
				break;
			case "exit":
			case "e":
				break;
			case "print":
				System.out.println(server.search(cityFromId, cityToId, date1, date2));
				break;
			default:
				System.out.println("unknown command");
				break;
			}
		} while (!(cmd.equals("exit") || cmd.equals("e")));
	}
	
	private static void addAdmin() {
		// TODO(Peng) addAdmin UI
		System.out.println("Please enter the Username and Password");
		String userName=scanner.nextLine();
		String password=scanner.nextLine();
		try {
			server.addAdmin(userName, password);
		} catch (PermissionDeniedException e) {
			
		}
	}

	private static void register() {
		// DONE(Zhu) register UI
		System.out.println("Please import your Username"); // XXX(Zhu) did you mean 'input'? :D
		String username;
		username=scanner.nextLine();
		System.out.println("Please import your identity card number");
		String idNumber;
		idNumber=scanner.nextLine();
		while(idNumber.length()>18||idNumber.length()<18){
			System.out.println("Please import the correct identity card number");
		idNumber=scanner.nextLine();
		}
		String password,password2;
		System.out.println("Please import your password");
		password=scanner.nextLine();
		do {
			System.out.println("Please import your password again");
		password2=scanner.nextLine();	
		} while (!(password.equals(password2)));
		System.out.println("You succeed in creating your account!");
		
		server.addPassenger(username, idNumber, password2);
		
	}

	private static void addFlight() {
		// DONE(Peng) addFlight UI
		/* FIXME(Peng) Please consider more when design your UI
		 * be sure to enhance the tolerance to user input, such as: empty input, error format, etc.
		 */
		System.out.println("flightName");
		String flightName=scanner.nextLine();
		// XXX(Peng) How about 1 stand for Jan. but add (month - 1) to final result?
		System.out.println("Please enter the Starttime,formatted with : year-month-date-hr-min-sec (Note: number 0 stand for January) :");
		String[] startime=scanner.nextLine().split("-");
		int year =Integer.parseInt(startime[0]);
		int month =Integer.parseInt(startime[1]);
		int date =Integer.parseInt(startime[2]);
		int hr =Integer.parseInt(startime[3]);
		int min =Integer.parseInt(startime[4]);
		int sec =Integer.parseInt(startime[5]);
		Date startTime = Flight.calendar(year, month, date, hr, min, sec);
		System.out.println("Please enter the arrivetime,formatted with : year-month-date-hr-min-sec (Note: number 0 stand for January) :");
		String[] arrivetime=scanner.nextLine().split("-");
		int year1 =Integer.parseInt(startime[0]);
		int month1 =Integer.parseInt(startime[1]);
		int date1 =Integer.parseInt(startime[2]);
		int hr1 =Integer.parseInt(startime[3]);
		int min1 =Integer.parseInt(startime[4]);
		int sec1 =Integer.parseInt(startime[5]);
		Date arriveTime = Flight.calendar(year1, month1, date1, hr1, min1, sec1);
		System.out.println("startCityID");
		int startCityID=scanner.nextInt();
		System.out.println("arriveCityID");
		int arriveCityID=scanner.nextInt();
		System.out.println("price");
		int price=scanner.nextInt();
		System.out.println("seatCapacity");
		int seatCapacity=scanner.nextInt();
		
		try {
			if (!server.createFlight(flightName, startTime, arriveTime, startCityID, arriveCityID, price, seatCapacity)) {
				System.out.println("error in city id, please retry");
			}
		} catch (PermissionDeniedException e) {
			
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
		} catch (PermissionDeniedException e) {
			// FIXME(Peng) be sure to finish exception handler!(also in other method)
			System.out.println("");
		}
	}

	private static void printHelp(boolean isMini) {
		// TODO(Dong) Help
		if (isMini) {
			System.out.println("Mini Help");
		} else {
			System.out.println("Full Help");
		}
	}

}
