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
		// DONE(Dong) UI design
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
			case "list":
			case "l":
				break;
			case "login":
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
				search(param);
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

	/*
	 * These are subUI or a wizard to lead User to do specific work
	 */
	private static void search(String[] param) {
		// TODO(Dong) search
		if (param != null && param.length != 0) {
			
		} else {
			System.out.println("please input the search parameter");
		}
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
		String idNumber;
		do {
			System.out.println("Please import the correct identity card number");
			idNumber=scanner.nextLine();
		} while(idNumber.length()>18||idNumber.length()<18);
		String password,password2;
		do {
			System.out.println("Please import your password");
			password=scanner.nextLine();
			System.out.println("Please import your password again");
			password2=scanner.nextLine();	
		} while (!(password.equals(password2)));
		System.out.println("You succeed in creating your account!");
		
		server.addPassenger(username, idNumber, password2);
		
	}

	private static void addFlight() {
		// DONE(Peng) addFlight UI
		System.out.println("flightName");
		String flightName=scanner.nextLine();
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
			server.createFlight(flightName, startTime, arriveTime, startCityID, arriveCityID, price, seatCapacity);
		} catch (PermissionDeniedException e) {
			
		}
	}
	private static void addCity() {
		// DONE(Peng) addCity UI
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
