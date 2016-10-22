package oculusbot.network.server;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main class for the server. Starts a controller and waits for a specific
 * keyboard input to close program.
 * 
 * @author Robert Meschkat
 *
 */
public class OculusbotServerMain {

	public static void main(String[] args) throws IOException {
		Controller controller = new Controller();
		controller.start();

		//wait till user wants to close server
		Scanner in = new Scanner(System.in);
		String line = "";
		while (!line.toLowerCase().equals("y")) {
			System.out.print("Exit (y/n)? ");
			line = in.nextLine();
		}
		in.close();
		controller.interrupt();
	}

}
