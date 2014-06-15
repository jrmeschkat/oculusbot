package com.github.jrmeschkat.oculusbot.server.clienthandling;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture.CaptureFrameTask;


public class HandleClientTask implements Runnable {
	
	public static final int FPS = 30;
	private Socket socket;
	private DataOutputStream out;
	private VideoCapture capture;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public HandleClientTask(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			capture = new VideoCapture(0);
			
			for(int i : CaptureFrameTask.getMetaInfo(capture)){
				out.writeInt(i);
			}
			
			ScheduledThreadPoolExecutor exe = new ScheduledThreadPoolExecutor(1);
			CaptureFrameTask captureFrameTask = new CaptureFrameTask(capture, out, socket, exe);
			exe.scheduleWithFixedDelay(captureFrameTask, 0, 1000/FPS, TimeUnit.MILLISECONDS);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
