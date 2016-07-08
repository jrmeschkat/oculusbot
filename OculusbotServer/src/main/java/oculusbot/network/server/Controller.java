package oculusbot.network.server;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import oculusbot.basic.PropertyLoader;
import oculusbot.basic.ServerProperties;
import oculusbot.basic.StatusThread;
import oculusbot.bot.BotControlThread;
import oculusbot.bot.StatusLED;
import oculusbot.pi.basics.Pins;
import oculusbot.video.SendVideoThread;

public class Controller extends StatusThread{
	private GpioController gpio;
	private BroadcastDiscoveryThread discovery;
	private BotControlThread bot;
	private SendVideoThread video;
	private StatusLED led;
	private PropertyLoader props;

	@Override
	protected void setup() {
		ignoreStatus = true;
		props = new PropertyLoader(ServerProperties.PROPERTY_FILENAME, ServerProperties.DEFAULT_PROPERTY_FILENAME);
		gpio = GpioFactory.getInstance();
		discovery = new BroadcastDiscoveryThread(props.getPropertyAsInt(ServerProperties.PORT_DISCOVERY));
		bot = new BotControlThread(props.getPropertyAsInt(ServerProperties.PORT_BOT), gpio);
		video = new SendVideoThread(props.getPropertyAsInt(ServerProperties.PORT_VIDEO));
		discovery.start();
		bot.start();
		video.start();
		
		led = new StatusLED(Pins.GPIO_05, gpio);
	}
	
	

	@Override
	protected void task() {
		led.setStatus(passthroughStatus(discovery, bot, video));
		pause(100);
	}

	@Override
	protected void shutdown() {
		discovery.interrupt();
		bot.interrupt();
		video.interrupt();
		led.shutdown();
		gpio.shutdown();
		
		waitForClosingThreads(discovery, bot, video);
	}
	
}
