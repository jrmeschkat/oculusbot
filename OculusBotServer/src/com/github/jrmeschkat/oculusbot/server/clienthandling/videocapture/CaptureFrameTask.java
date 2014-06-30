package com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture;

import java.util.LinkedList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class CaptureFrameTask implements Runnable {

	public static final int QUALITY = 50;
	private VideoCapture capture;
	private MatOfInt params;
	private LinkedList<Frame> frames;

	public CaptureFrameTask(VideoCapture capture, LinkedList<Frame> frames) {
		this.capture = capture;
		this.frames = frames;
		params = new MatOfInt(Highgui.CV_IMWRITE_JPEG_QUALITY, QUALITY);
	}

	@Override
	public void run() {
		// capture frame from webcam
		Mat frame = new Mat();
		MatOfByte buffer = new MatOfByte();
		capture.read(frame);
		// long timestamp = System.currentTimeMillis();
		Highgui.imencode(".jpg", frame, buffer, params);

		// get data from stored frame
		// byte[] data = buffer.toArray();

		// send data
		frames.addFirst(new Frame(buffer));
	}

}
