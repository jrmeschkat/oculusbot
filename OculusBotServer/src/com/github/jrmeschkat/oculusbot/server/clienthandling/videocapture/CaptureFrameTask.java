package com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class CaptureFrameTask implements Runnable {

	public static final int QUALITY = 50;
	private VideoCapture capture;
	private DataOutputStream out;
	private Socket socket;
	private ScheduledThreadPoolExecutor exe;
	private MatOfInt params;

	public CaptureFrameTask(VideoCapture capture, DataOutputStream out, Socket socket, ScheduledThreadPoolExecutor exe) {
		this.capture = capture;
		this.out = out;
		this.socket = socket;
		this.exe = exe;
		params = new MatOfInt(Highgui.CV_IMWRITE_JPEG_QUALITY, QUALITY); 
	}

	@Override
	public void run() {
		try {
			//capture frame from webcam
			Mat frame = new Mat();
			MatOfByte buffer = new MatOfByte();
			capture.read(frame);
			long timestamp = System.currentTimeMillis();
			Highgui.imencode(".jpg", frame, buffer, params);
			
			//get data from stored frame
			byte[] data = buffer.toArray();
			
			//send data
			//TODO think about data compression
			//TODO find out if synchronism is a problem
			out.writeLong(timestamp);
			out.writeInt(data.length);
			out.write(data, 0, data.length);
		} catch (SocketException e1){
			exe.shutdown();
			
			if(capture != null){
				capture.release();
			}
			
			if(out != null){
				try { out.close(); } catch (IOException e) {}
			}
			
			
			if(socket != null){
				try { socket.close(); } catch (IOException e) {}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
