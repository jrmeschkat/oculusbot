package com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class CaptureFrameTask implements Runnable {

	private VideoCapture capture;
	private DataOutputStream out;
	private Socket socket;
	private ScheduledThreadPoolExecutor exe;

	public CaptureFrameTask(VideoCapture capture, DataOutputStream out, Socket socket, ScheduledThreadPoolExecutor exe) {
		this.capture = capture;
		this.out = out;
		this.socket = socket;
		this.exe = exe;
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
	
	public static int[] getMetaInfo(VideoCapture capture){
		int[] info = new int[4];
		Mat frame = new Mat();
		capture.read(frame);
		int size = frame.width() * frame.height() * frame.channels();
		info[0] = frame.rows();
		info[1] = frame.cols();
		info[2] = frame.type();
		info[3] = size;
		return info;
	}

}
