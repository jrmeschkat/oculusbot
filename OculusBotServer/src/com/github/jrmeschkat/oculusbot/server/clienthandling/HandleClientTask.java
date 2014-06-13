package com.github.jrmeschkat.oculusbot.server.clienthandling;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
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
			
			CaptureFrameTask captureFrameTask = new CaptureFrameTask(capture, out);
			new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(captureFrameTask, 0, 1000/FPS, TimeUnit.MILLISECONDS);
			
			synchronized (this) {
				try {
					System.out.print("Waiting for the end ("+Thread.currentThread()+")...");
					wait();
					System.out.println("DONE");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			if(capture != null){
				capture.release();
			}
			
			if(out != null){
				try { out.close(); } catch (IOException e1) {}
			}
			
			if(socket != null){
				try { socket.close(); } catch (IOException e) {}
			}
		}		
	}
}
