import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;

import org.opencv.core.MatOfByte;


public class ReceiveFramesTask extends Thread {
	
	private LinkedList<Frame> frames;
	private DatagramSocket socket;
	
	public ReceiveFramesTask(DatagramSocket socket, LinkedList<Frame> frames){
		this.frames = frames;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		byte[] data;
		DatagramPacket p;
		while(!isInterrupted()){
			try {
				//FIXME buffer size
				data = new byte[32768];
				p = new DatagramPacket(data, data.length);
				socket.receive(p);
				MatOfByte buffer = new MatOfByte(data);
				//FIXME timestamp
				frames.addFirst(new Frame(buffer, System.currentTimeMillis()));
			} catch (SocketException se) {} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		socket.close();
	}
	

}
