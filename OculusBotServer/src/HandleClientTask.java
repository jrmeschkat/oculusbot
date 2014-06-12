import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;


public class HandleClientTask {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	public HandleClientTask(Socket socket){
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			//TODO replace with threads
			CaptureFrameTask captureFrameTask = new CaptureFrameTask(new VideoCapture(0));
			captureFrameTask.run();
			Mat frame = captureFrameTask.getFrame();
			int size = frame.width()*frame.height()*frame.channels();
			byte[] data = new byte[size];
			frame.get(0, 0, data);
			out.writeInt(frame.rows());
			out.writeInt(frame.cols());
			out.writeInt(frame.type());
			out.writeInt(data.length);
			out.write(data, 0, data.length);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			if(socket != null){
				try { socket.close(); } catch (IOException e) {}
			}
		}
	}
}
