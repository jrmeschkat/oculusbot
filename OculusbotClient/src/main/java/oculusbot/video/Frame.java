package oculusbot.video;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class Frame {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private Mat mat;
	private long timeElapsed;
	private long timeReceived;
	private double ping;

	public Mat getMat() {
		return mat;
	}

	public Frame(byte[] data, long timeElapsed, long timeReceived, double ping) {
		mat = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		this.timeElapsed = timeElapsed;
		this.timeReceived = timeReceived;
		this.ping = ping;
	}

	public double getLatency(long time) {
		return (((time - timeReceived) + timeElapsed) / 1000000d) + ping;
	}
}
