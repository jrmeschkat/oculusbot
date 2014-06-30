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
import org.opencv.highgui.VideoCapture;

import com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture.CaptureFrameTask;
import com.github.jrmeschkat.oculusbot.server.clienthandling.videocapture.Frame;

public class SendFramesTask implements Runnable {

	public static final int FPS = 30;
	private DatagramSocket socket;
	private InetAddress clientAddress;
	private int port;
	private VideoCapture capture;
	private	LinkedList<Frame> frames = new LinkedList<Frame>();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public SendFramesTask(DatagramSocket socket, InetAddress clientAddress, int port) {
		this.socket = socket;
		this.clientAddress = clientAddress;
		this.port = port;
		capture = new VideoCapture(0);
		CaptureFrameTask captureFrameTask = new CaptureFrameTask(capture, frames);
		new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(captureFrameTask, 0, 1000 / FPS, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		DatagramPacket p;
		while (true) {
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
	}
}
