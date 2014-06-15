import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import org.opencv.core.Mat;


public class ReceiveFramesTask implements Runnable {
	
	private LinkedList<Mat> frames;
	private DataInputStream in;
	private static int rows = -1;
	private static int cols = -1;
	private static int type = -1;
	private static int length = -1;
	private static boolean readMetaInfo = false;
	
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
				if(!readMetaInfo){
					rows = in.readInt();
					cols = in.readInt();
					type = in.readInt();
					length = in.readInt();
					System.out.println("Rows: "+rows+"\tCols: "+cols+"\tType: "+type+"\tLength: "+length);
					readMetaInfo = true;
				}
				byte[] data = new byte[length];
				in.readFully(data);
				frame = new Mat(rows, cols, type);
				frame.put(0, 0, data);
				frames.addFirst(frame);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
