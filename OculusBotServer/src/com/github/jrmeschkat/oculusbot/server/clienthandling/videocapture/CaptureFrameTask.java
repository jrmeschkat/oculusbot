package com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture;

import java.io.DataOutputStream;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class CaptureFrameTask implements Runnable {

	private VideoCapture capture;
	private DataOutputStream out;

	public CaptureFrameTask(VideoCapture capture, DataOutputStream out) {
		this.capture = capture;
		this.out = out;
	}

	@Override
	public void run() {
		try {
			//capture frame from webcam
			Mat frame = new Mat();
			capture.read(frame);
			
			//get data from stored frame
			int size = frame.width() * frame.height() * frame.channels();
			byte[] data = new byte[size];
			frame.get(0, 0, data);
			
			//send data
			//TODO think about data compression
			//TODO find out if synchronism is a problem  
			out.writeInt(frame.rows());
			out.writeInt(frame.cols());
			out.writeInt(frame.type());
			out.writeInt(data.length);
			out.write(data, 0, data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
