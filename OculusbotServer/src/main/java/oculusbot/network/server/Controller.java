package oculusbot.network.server;

import org.lwjgl.glfw.GLFW;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import oculusbot.basic.PropertyLoader;
import static oculusbot.basic.ServerProperties.*;

import oculusbot.basic.StatusThread;
import oculusbot.bot.BotControlThread;
import oculusbot.bot.StatusLED;
import oculusbot.pi.basics.Pins;
import oculusbot.video.SendVideoThread;

/**
 * Class that handles communication between the differnet parts of the program.
 * @author Robert Meschkat
 *
 */
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
		//load the property file
		props = new PropertyLoader(PROPERTY_FILENAME, DEFAULT_PROPERTY_FILENAME);
		//create the GPIO controller instance which will be used throughout this program
		gpio = GpioFactory.getInstance();
		//create the communications thread
		com = new CommunicationsThread(props.getPropertyAsInt(PORT_DISCOVERY), this);
		
		//load some properties for the video thread
		int camWidth = props.getPropertyAsInt(CAM_WIDTH);
		int camHeight = props.getPropertyAsInt(CAM_HEIGHT);
		msg("Cam resolution: " + camWidth + " x " + camHeight);
		//start the video and bot control thread
		bot = new BotControlThread(gpio);
		video = new SendVideoThread(props.getPropertyAsInt(PORT_VIDEO), camWidth, camHeight);
		com.start();
		bot.start();
		video.start();

		led = new StatusLED(Pins.GPIO_26, gpio);
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

	/**
	 * Tells the video thread to add a client to the receiver list.
	 * @param ip IP of the client
	 */
	public void registerClient(String ip) {
		video.registerClient(ip);
	}

	/**
	 * Tells the video thread to remove a client from the receiver list.
	 * @param ip IP of the client
	 */
	public void deregisterClient(String ip) {
		video.deregisterClient(ip);
	}

	/**
	 * Handles the received keyboard input of the client.
	 * @param key Key that determines the operation
	 */
	public void keyReleased(int key) {
		if (key == GLFW.GLFW_KEY_S) {
			video.switchCameras();
		}
	}

	/**
	 * Tells the bot control thread to update the target motor position.
	 * @param data New position data.
	 */
	public void setPosition(double[] data) {
		if (data != null && data.length > 2)
			bot.set(data[0], data[1], data[2]);
	}
}
