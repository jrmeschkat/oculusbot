import java.io.BufferedOutputStream;
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
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			//TODO replace with threads
			CaptureFrameTask captureFrameTask = new CaptureFrameTask(new VideoCapture(0));
			captureFrameTask.run();
			Mat frame = captureFrameTask.getFrame();
			int size = frame.width()*frame.height()*frame.channels();
			byte[] data = new byte[size];
			frame.get(0, 0, data);
//			Mat test = new Mat(frame.rows(), frame.cols(), frame.type());
//			test.put(0, 0, data);
//			Highgui.imwrite("test.jpg", test);
			out.write(frame.rows());
			out.write(frame.cols());
			out.write(frame.type());
			out.write(size);
			out.write(data);
			out.flush();
//			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
//		finally {
//			if(socket != null){
//				try { socket.close(); } catch (IOException e) {}
//			}
//		}
	}
}
