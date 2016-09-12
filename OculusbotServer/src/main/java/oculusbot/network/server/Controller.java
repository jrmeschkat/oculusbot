package oculusbot.network.server;

import org.lwjgl.glfw.GLFW;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import oculusbot.basic.PropertyLoader;
import static oculusbot.basic.ServerProperties.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import oculusbot.basic.StatusThread;
import oculusbot.bot.BotControlThread;
import oculusbot.bot.StatusLED;
import oculusbot.pi.basics.Pins;
import oculusbot.video.SendVideoThread;

public class Controller extends StatusThread {
	private GpioController gpio;
	private CommunicationsThread com;
	private BotControlThread bot;
	private SendVideoThread video;
	private StatusLED led;
	private PropertyLoader props;

	@Override
	protected void setup() {
		ignoreStatus = true;
		props = new PropertyLoader(PROPERTY_FILENAME, DEFAULT_PROPERTY_FILENAME);
		gpio = GpioFactory.getInstance();
		com = new CommunicationsThread(props.getPropertyAsInt(PORT_DISCOVERY), this);
		int camWidth = props.getPropertyAsInt(CAM_WIDTH);
		int camHeight = props.getPropertyAsInt(CAM_HEIGHT);
		msg("Cam resolution: " + camWidth + " x " + camHeight);
		bot = new BotControlThread(gpio);
		video = new SendVideoThread(props.getPropertyAsInt(PORT_VIDEO), camWidth, camHeight);
		com.start();
		bot.start();
		video.start();

		led = new StatusLED(Pins.GPIO_05, gpio);
	}

	@Override
	protected void task() {
		led.setStatus(passthroughStatus(com, bot, video));
		pause(100);
	}

	@Override
	protected void shutdown() {
		com.interrupt();
		bot.interrupt();
		video.interrupt();
		led.shutdown();
		gpio.shutdown();

		waitForClosingThreads(com, bot, video);
	}

	public void registerClient(String ip) {
		video.registerClient(ip);
	}

	public void deregisterClient(String ip) {
		video.deregisterClient(ip);
	}

	public void keyReleased(int key) {
		if (key == GLFW.GLFW_KEY_S) {
			video.switchCameras();
		}
	}

	public void setPosition(double[] data) {
		if (data != null && data.length > 2)
			bot.set(data[0], data[1], data[2]);
	}
}
