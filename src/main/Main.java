package main;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// TODO(Dong) UI design
		MainServer server = new MainServer();
		Scanner scanner = new Scanner(System.in);
		String string = "";
		String[] param;
		printHelp(true);
		while (!string.equals("exit")) {
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
				printHelp(false);
				break;
			case "exit":
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

	private static void printHelp(boolean isMini) {
		// TODO(Dong) Help
		if (isMini) {
			System.out.println("Mini Help");
		} else {
			System.out.println("Full Help");
		}
	}

}
