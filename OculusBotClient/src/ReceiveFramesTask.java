import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;


public class ReceiveFramesTask implements Runnable {
	
	private LinkedList<MatOfByte> frames;
	private DataInputStream in;
	private boolean running = true;
	
	public ReceiveFramesTask(Socket socket, LinkedList<MatOfByte> frames){
		this.frames = frames;
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(running){
			try {
				int size = in.readInt();
				byte[] data = new byte[size];
				in.readFully(data);
				MatOfByte buffer = new MatOfByte(data);
				frames.addFirst(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try { in.close(); } catch (IOException e) {}
	}
	
	public void stop(){
		running = false;
	}

}
