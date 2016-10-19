package oculusbot.video;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import oculusbot.network.NetworkThread;
import oculusbot.network.test.PingThread;

public class ReceiveVideoThread extends NetworkThread {

	private static final int PACKET_SIZE = 32000;
	private Frame frame;
	private PingThread ping;

	public Frame getFrame() {
		return frame;
	}

	public ReceiveVideoThread(int port, String ip) {
		super(port);
		ping = new PingThread(ip, PACKET_SIZE);
	}

	@Override
	protected void setup() {
		super.setup();
		setPacketSize(PACKET_SIZE);
	}

	@Override
	protected void doNetworkOperation() throws IOException {
		DatagramPacket packet;
		long timeReceived = 0;
		try {
			packet = receive();
			timeReceived = System.nanoTime();
		} catch (SocketTimeoutException e) {
			return;
		}

		byte[] data = packet.getData();
		long timeElapsed = byteArrayToLong(Arrays.copyOf(data, Long.BYTES));
		data = Arrays.copyOfRange(data, Long.BYTES, data.length);
		
		frame = new Frame(data, timeElapsed, timeReceived, ping.getPing());

	}
	
	@Override
	protected void shutdown() {
		super.shutdown();
		ping.interrupt();
	}

	private long byteArrayToLong(byte[] b) {
		long result = 0;

		for (int i = 0; i < b.length; i++) {
			result += ((long) b[i] & 0xff) << (i * 8);
		}

		return result;
	}
}
