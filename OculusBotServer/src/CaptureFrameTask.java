
import java.util.Arrays;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;


public class CaptureFrameTask {
	
	private VideoCapture capture = null;
	private Mat frame;
	
	public Mat getFrame() { return frame; }
	
	public CaptureFrameTask(VideoCapture capture){
		this.capture = capture;
	}
	
//	@Override
	public void run() {
		frame = new Mat();
		capture.read(frame);
		capture.release();
//		img = MatToBufferedImage(frame);
	}

}
