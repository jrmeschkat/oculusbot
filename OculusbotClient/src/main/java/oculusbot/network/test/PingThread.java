package oculusbot.network.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import oculusbot.basic.StatusThread;

public class PingThread extends StatusThread {
	public static final int DEFAULT_PACKET_SIZE = 2048;
	public static final int PACKET_COUNT = 5;
	private String cmd;
	private String host;
	private int packetSize;
	private boolean windows = false;
	private double ping = 0;

	public double getPing() {
		return ping;
	}
	
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

	public PingThread(String host, int packetSize) {
		super();
		this.host = host;
		this.packetSize = packetSize;
		if (System.getProperty("os.name").contains("Windows")) {
			windows = true;
		}
	}

	public PingThread(String host) {
		this(host, DEFAULT_PACKET_SIZE);
	}

	@Override
	protected void setup() {
		cmd = "ping ";
		if (windows) {
			cmd += "-n " + PACKET_COUNT + " -l " + packetSize;
		} else {
			cmd += "-c " + PACKET_COUNT + " -s " + packetSize;
		}
		cmd += " " + host;
	}

	@Override
	protected void task() {
		try {
			Process pro = Runtime.getRuntime().exec(cmd);
			pro.waitFor();

			BufferedReader r = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			ArrayList<String> output = new ArrayList<>();
			String line = "";
			while ((line = r.readLine()) != null) {
				output.add(line);
			}

			ping = parseAverageTime(output.toArray(new String[output.size()]));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void shutdown() {

	}

	private double parseAverageTime(String[] output) {
		double average = 0;

		for (String line : output) {
			line = line.toLowerCase();
			if (line.contains("statisti")) {
				break;
			}
			
			String[] data = line.split(" ");
			for (String s : data) {
				if (s.startsWith("zeit") || s.startsWith("time")) {
					average += Double.valueOf(s.replaceAll("[^0-9.]+", ""));
				}
			}

		}

		return average / PACKET_COUNT;
	}

}
