import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

import org.opencv.core.MatOfByte;


public class ReceiveFramesTask implements Runnable {
	
	private LinkedList<Frame> frames;
	private DatagramSocket socket;
	private boolean running = true;
	
	public ReceiveFramesTask(DatagramSocket socket, LinkedList<Frame> frames){
		this.frames = frames;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		byte[] data;
		DatagramPacket p;
		
		while(running){
			try {
				data = new byte[32768];
				p = new DatagramPacket(data, data.length);
				socket.receive(p);
				MatOfByte buffer = new MatOfByte(data);
				//FIXME
				frames.addFirst(new Frame(buffer, System.currentTimeMillis()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		socket.close();
	}
	
	public void stop(){
		running = false;
	}

}
