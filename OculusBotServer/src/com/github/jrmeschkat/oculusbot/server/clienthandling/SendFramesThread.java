package com.github.jrmeschkat.oculusbot.server.clienthandling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture.CaptureFrameTask;
import com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture.Frame;

public class SendFramesThread extends Thread {

	public static final int FPS = 30;
	private DatagramSocket socket;
	private InetAddress clientAddress;
	private int port;
	private VideoCapture capture;
	private	LinkedList<Frame> frames = new LinkedList<Frame>();
	private ScheduledThreadPoolExecutor exe;
	private boolean dummyMode = false;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public SendFramesThread(DatagramSocket socket, InetAddress clientAddress, int port, int videoDeviceID) {
		this.socket = socket;
		this.clientAddress = clientAddress;
		this.port = port;
		capture = new VideoCapture(videoDeviceID);
		if(capture.isOpened()){
			CaptureFrameTask captureFrameTask = new CaptureFrameTask(capture, frames);
			exe = new ScheduledThreadPoolExecutor(1);
			exe.scheduleWithFixedDelay(captureFrameTask, 0, 1000 / FPS, TimeUnit.MILLISECONDS);
		} else {
			dummyMode = true;
		}
	}

	@Override
	public void run() {
		DatagramPacket p;
		if(!dummyMode){ //only executed if camera detected
			while (!isInterrupted()) {
				try {
					Frame f = frames.removeLast();
					byte[] buffer = f.getFrame().toArray();
					p = new DatagramPacket(buffer, buffer.length, clientAddress, port);
					socket.send(p);
				} catch (NoSuchElementException e){} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else { //no camera found
			Mat dummy = Highgui.imread("dummy.jpg");
			MatOfByte buffer = new MatOfByte();
			MatOfInt params = new MatOfInt(Highgui.CV_IMWRITE_JPEG_QUALITY, 10);
			Highgui.imencode(".jpg", dummy, buffer, params);
			byte[] data = buffer.toArray();
			while (!isInterrupted()){
				try {
					p = new DatagramPacket(data, data.length, clientAddress, port);
					socket.send(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					sleep(30);
				} catch (InterruptedException e) {}
			}
		}
		
		if(exe != null){
			exe.shutdown();
		}
		
		capture.release();
	}
	
}
