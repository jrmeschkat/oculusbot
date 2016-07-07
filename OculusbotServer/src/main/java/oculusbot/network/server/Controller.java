package oculusbot.network.server;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

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



	@Override
	protected void setup() {
		ignoreStatus = true;
		gpio = GpioFactory.getInstance();
		discovery = new BroadcastDiscoveryThread(1337);
		bot = new BotControlThread(1338, gpio);
		video = new SendVideoThread(1339);
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
