package oculusbot.video;

import java.io.IOException;
import java.util.LinkedList;

import oculusbot.network.NetworkThread;

public class SendVideoThread extends NetworkThread {
	private FrameGrabberThread frameGrabber;
	private LinkedList<String> clients;

	public SendVideoThread(int port) {
		super(port);
		clients = new LinkedList<>();
	}

	@Override
	public Status getStatus() {
		return passthroughStatus(frameGrabber);
	}

	@Override
	protected void setup() {
		super.setup();
		frameGrabber = new FrameGrabberThread();
		frameGrabber.start();
	}

	public void switchCameras() {
		frameGrabber.switchCameras();
	}

	@Override
	protected void doNetworkOperation() throws IOException {
		if(clients.isEmpty()){
			pause(100);
			return;
		}
		byte[] data = frameGrabber.grabFrameAsByte();
		if (data != null) {
			for(String ip : clients){
				send(data, ip);
			}
		}
	}

	@Override
	protected void shutdown() {
		frameGrabber.interrupt();
		waitForClosingThreads(frameGrabber);
	}

	public void registerClient(String ip) {
		for(String client : clients){
			if(client.equals(ip)){
				return;
			}
		}
		clients.add(ip);
	}

	public void deregisterClient(String ip) {
		for(int i = 0; i < clients.size(); i++){
			if (clients.get(i).equals(ip)) {
				clients.remove(i);
				return;
			}
		}
	}
	

}
