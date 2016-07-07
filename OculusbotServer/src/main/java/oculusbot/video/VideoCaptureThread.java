package oculusbot.video;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import oculusbot.basic.StatusThread;

public class VideoCaptureThread extends StatusThread {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private static final int WIDTH = 400;
	private static final int HEIGHT = 300;
	
	private VideoCapture cam;
	private Mat frame;
	private int camId;

	public Mat getFrame() {
		return frame;
	}

	public VideoCaptureThread(int camId) {
		this.camId = camId;
	}

	
	@Override
	protected void setup() {
		cam = new VideoCapture();
		cam.open(camId);
		cam.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, WIDTH);
		cam.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT);

		if (!cam.isOpened()) {
			throw new IllegalStateException("Couldn't open cam: " + camId);
		}
	}
	
	@Override
	protected void task() {
		Mat buffer = new Mat();
		cam.grab();
		cam.retrieve(buffer);
		frame = buffer;
	}
	
	@Override
	protected void shutdown() {
		cam.release();
	}
}
