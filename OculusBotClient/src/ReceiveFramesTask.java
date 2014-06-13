import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import org.opencv.core.Mat;


public class ReceiveFramesTask implements Runnable {
	
	private LinkedList<Mat> frames;
	private DataInputStream in;
	
	public ReceiveFramesTask(Socket socket, LinkedList<Mat> frames){
		this.frames = frames;
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		//FIXME find better condition
		Mat frame;
		while(true){
			try {
				int rows = in.readInt();
				int cols = in.readInt();
				int type = in.readInt();
				int length = in.readInt();
				byte[] data = new byte[length];
				in.readFully(data);
				frame = new Mat(rows, cols, type);
				frame.put(0, 0, data);
				frames.addFirst(frame);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
