package main;

import java.util.Date;
import java.util.Scanner;

import exceptions.PermissionDeniedException;
import exceptions.StatusUnavailableException;
import flight.Flight;

public class Main {

	static MainServer server = new MainServer();
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws Throwable {
		// TODO(Dong) UI design
		String string = "";
		String[] param;
		printHelp(true);
		while (!(string.equals("exit") || string.equals("e"))) {
			System.out.print(">");
			string = scanner.nextLine();
			string = string.replaceAll("\\s+", " ");
			if (string.contains(" ")) {
				if (string.equals(" ")) {
					continue;
				} else if (string.startsWith(" ")) {
					string = string.replaceAll("^\\s+", "");
				}
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
			case "login":
			case "l":
				if (param != null && param.length == 2) {
					if (server.Login(param[0], param[1])) {
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
	
	private static void add(String[] param) {
		// DONE(Dong) add
		if (param != null && param.length > 0) {
			switch (param[0]) {
			case "city":
				addCity();
				break;
			case "flight":
				addFlight();
				break;
			case "user":
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
		// TODO(Dong) delete
		if (param != null && param.length >= 2) {
			switch (param[0]) {
			case "flight":
				try {
					for (int i = 0; i < param.length; i++) {
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
			// TODO(Dong) unfinished
			default:
				System.out.println("You can only delete a city, flight or user");
				break;
			}
		} else {
			System.out.println("Format error");
		}
	}

	/*
	 * These are subUI or a wizard to lead User to do specific work
	 */
	private static void search() {
		// TODO(Dong) search
		
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
		System.out.println("Please import your Username");
		String username;
		username=scanner.nextLine();
		System.out.println("Please import your identity card number");
		String IDNUMBER;
		IDNUMBER=scanner.nextLine();
		while(IDNUMBER.length()>18||IDNUMBER.length()<18){
			System.out.println("Please import the correct identity card number");
		IDNUMBER=scanner.nextLine();
		}
		String password,password2;
		System.out.println("Please import your password");
		password=scanner.nextLine();
		do {
			System.out.println("Please import your password again");
		password2=scanner.nextLine();	
		} while (!(password.equals(password2)));
		System.out.println("You succeed in creating your account!");
		
		server.addUser(username, IDNUMBER, password2);
		
	}

	private static void addFlight() {
		// TODO(Peng) addFlight UI
		System.out.println("flightName");
		String flightName=scanner.nextLine();
		System.out.println("starttime formatted with:year-month-date-hr-min-sec");
		String[] startime=scanner.nextLine().split("-");
		int year =Integer.parseInt(startime[0]);
		int month =Integer.parseInt(startime[1]);
		int date =Integer.parseInt(startime[2]);
		int hr =Integer.parseInt(startime[3]);
		int min =Integer.parseInt(startime[4]);
		int sec =Integer.parseInt(startime[5]);
		Date startTime = Flight.calendar(year, month, date, hr, min, sec);
		System.out.println("arrivetime formatted with:year-month-date-hr-min-sec");
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
			    server.createFlight(flightName, startTime, arriveTime, startCityID, arriveCityID, price, seatCapacity);
					} catch (PermissionDeniedException e) {
		}
	}
	private static void addCity() {
		// TODO(Peng) addCity UI
		System.out.println("Please enter a valid city name" );
		String cityname=scanner.nextLine();		
		try {
			server.addCity(cityname);
		} catch (PermissionDeniedException e) {
			
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
