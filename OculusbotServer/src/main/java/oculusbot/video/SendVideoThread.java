package oculusbot.video;

import java.io.IOException;
import java.net.SocketException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import oculusbot.network.NetworkThread;

public class SendVideoThread extends NetworkThread {
	private FrameGrabberThread frameGrabber;

	public SendVideoThread(int port) {
		super(port);
		
	}
	
	@Override
	public Status getStatus() {
		return passthroughStatus(frameGrabber);
	}
	
	@Override
	protected void setup() {
		super.setup();
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		frameGrabber = new FrameGrabberThread();
		frameGrabber.start();
	}
	
	public void switchCameras(){
		frameGrabber.switchCameras();
	}

	@Override
	protected void doNetworkOperation() throws IOException {
		byte[] data = frameGrabber.grabFrameAsByte();
		if (data != null){
			send(data);
		}
	}

	@Override
	protected void shutdown() {
		frameGrabber.interrupt();
		waitForClosingThreads(frameGrabber);
	}

}
