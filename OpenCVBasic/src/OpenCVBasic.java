import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;


public class OpenCVBasic {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoCapture cap = new VideoCapture(0);
		Mat image = new Mat();
		cap.read(image);
		cap.release();
		Highgui.imwrite("test.jpg", image);
	}

}
