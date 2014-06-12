import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class StartClient {
	public static final String HOST = "localhost";
	public static final int PORT = 1337;
	
	private Socket socket = null;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public static void main(String[] args) {
		new StartClient();
	}
	
	public StartClient(){
		try {
			socket = new Socket(HOST, PORT);
			BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
			int rows = in.read();
			int cols = in.read();
			int type = in.read();
			int size = in.read();
			byte[] data = new byte[size];
			for(int i = 0; i < data.length; i++){
				data[i] = (byte)in.read();
			}
			Mat frame = new Mat(rows, cols, type);
			frame.put(0, 0, data);
			Highgui.imwrite("frame.jpg", frame);
			in.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
//		finally{
//			if(socket != null){
//				try {socket.close();} catch (IOException e) {}
//			}
//		}
	}

}
