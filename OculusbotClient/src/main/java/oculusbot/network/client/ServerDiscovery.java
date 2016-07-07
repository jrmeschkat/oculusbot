package oculusbot.network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import oculusbot.network.NetworkConstants;

public class ServerDiscovery {
	private DatagramSocket socket;
	private static final int TIMEOUT = 5000;

	public InetAddress getServerIP(int port) {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			socket.setSoTimeout(TIMEOUT);
			
			while(true){
				System.out.println("\nTrying to find Server: ");
				
				System.out.print("Sending broadcast...");
				byte[] buf = NetworkConstants.OB_REQUEST_SERVER_IP.getBytes();
				DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(NetworkConstants.BROADCAST_IP), port);
				socket.send(packet);
				System.out.println("DONE");
				
				System.out.print("Waiting for answer...");
				buf = new byte[NetworkConstants.DEFAULT_PACKAGE_SIZE];
				DatagramPacket in = new DatagramPacket(buf, buf.length);
				try{
					socket.receive(in);
					
				} catch(SocketTimeoutException e){
					System.err.println("ERROR: Couldn't find server.");
					continue;
				}
				
				String msg = new String(in.getData()).trim();
				if(msg.equals(NetworkConstants.OB_RESPONSE_SERVER_IP)){
					System.out.println("DONE");
					return in.getAddress();
				} else{
					System.err.println("ERROR: Incorrect answer.");
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
