package main;

import java.util.Scanner;

import exceptions.PermissionDeniedException;

public class Main {

	static MainServer server = new MainServer();
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
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
				dalete(param);
				break;
			default:
				if (!string.equals("")) {
					System.out.println("Unknown command: Type 'help' for more information.");
				}
				break;
			}
		}
		scanner.close();
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

	private static void dalete(String[] param) {
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
		
	}

	private static void register() {
		// TODO(Zhu) register UI
		
	}

	private static void addFlight() {
		// TODO(Peng) addFlight UI
		
	}

	private static void addCity() {
		// TODO(Peng) addCity UI
		System.out.println("Please enter a valid city name" );
//		Scanner input = new Scanner(System.in); // no need to create a new scanner, you can use static field
//-		String cityname=input.nextLine();
		String cityname=scanner.nextLine();		
//		MainServer Acity=new MainServer(); //never create new instance of MainServer, user static field instead
		try {
//-			Actiy.addCity(cityname);
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
