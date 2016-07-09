package oculusbot.video;

import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import oculusbot.basic.StatusThread;

public class FrameGrabberThread extends StatusThread {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private static final int QUALITY = 25;
	private MatOfByte buffer;
	private VideoCaptureThread leftThread;
	private VideoCaptureThread rightThread;
	private boolean switchCams = false;
	
	@Override
	public Status getStatus() {
		return passthroughStatus(leftThread, rightThread);
	}
	
	public void switchCameras(){
		switchCams = !switchCams;
	}

	public byte[] grabFrameAsByte() {
		try {
			return buffer.toArray();
		} catch (RuntimeException e) {
			return null;
		}

	}

	@Override
	protected void setup() {
		this.buffer = new MatOfByte();
		leftThread = new VideoCaptureThread(0);
		rightThread = new VideoCaptureThread(1);
		leftThread.start();
		rightThread.start();		
	}

	@Override
	protected void task() {
		Mat m = new Mat();
		Mat left = new Mat();
		Mat right = new Mat();
		if(switchCams){
			left = leftThread.getFrame();
			right = rightThread.getFrame();
		} else{
			right = leftThread.getFrame();
			left = rightThread.getFrame();
		}
		if (left == null || left.empty() || right == null || right.empty()) {
			return;
		}

		Core.hconcat(Arrays.asList(new Mat[] { left, right }), m);
		MatOfByte buf = new MatOfByte();
		MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, QUALITY);
		Imgcodecs.imencode(".jpg", m, buf, params);
		buffer = buf;		
	}

	@Override
	protected void shutdown() {
		leftThread.interrupt();
		rightThread.interrupt();
		waitForClosingThreads(leftThread, rightThread);
	}

}
