import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

import org.opencv.core.MatOfByte;


public class ReceiveFramesTask extends Thread {
	
	private LinkedList<Frame> framesLeft;
	private LinkedList<Frame> framesRight;
	private DatagramSocket socket;
	private boolean running = true;
	
	public ReceiveFramesTask(DatagramSocket socket, LinkedList<Frame> framesLeft, LinkedList<Frame> framesRight){
		this.framesRight = framesRight;
		this.framesLeft = framesLeft;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		byte[] data;
		DatagramPacket p;
		boolean left = true;
		while(!isInterrupted()){
			try {
				//FIXME buffer size
				data = new byte[32768];
				p = new DatagramPacket(data, data.length);
				socket.receive(p);
				MatOfByte buffer = new MatOfByte(data);
				//FIXME get "real" left and right frame
				if(left){
					//FIXME timestamp
					framesLeft.addFirst(new Frame(buffer, System.currentTimeMillis()));
					left = false;
				} else {
					framesRight.addFirst(new Frame(buffer, System.currentTimeMillis()));
					left = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		socket.close();
	}
	

}
