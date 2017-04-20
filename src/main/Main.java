package main;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// TODO(Dong) UI design
		MainServer server = new MainServer();
		Scanner scanner = new Scanner(System.in);
		String string = "";
		String param = "";
		printHelp(true);
		while (!string.equals("exit")) {
			System.out.print(">");
			string = scanner.nextLine();
			if (string.contains(" ")) {
				string = string.replaceAll("\\s+", " ");
				try {
					param = string.split(" ")[1];
					string = string.split(" ")[0];
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					System.out.println("Unknown command: Type 'help' for more information.");
					string = "";
					param = "";
				}
			}
			else {
				param = "";
			}
			switch (string) {
			case "help":
				printHelp(false);
				break;
			case "exit":
				break;
			default:
				if (!string.equals("")) {
					System.out.println("Unknown command: Print 'help' for more information.");
				}
				break;
			}
		}
		scanner.close();
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
