package oculusbot.network.server;

import java.io.IOException;
import java.util.Scanner;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import oculusbot.bot.BotControlThread;
//import oculusbot.bot.BotControlThread;
import oculusbot.video.SendVideoThread;

public class OculusbotServerMain {

	public static void main(String[] args) throws IOException {
		Controller controller = new Controller();
		controller.start();
		
		Scanner in = new Scanner(System.in);
		String line = "";
		while(!(line = in.nextLine()).toLowerCase().equals("y")){
			System.out.print("Exit (y/n)? ");
		}
		in.close();
		controller.interrupt();
	}

}
