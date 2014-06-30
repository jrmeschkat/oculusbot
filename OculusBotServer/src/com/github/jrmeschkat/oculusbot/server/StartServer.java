package com.github.jrmeschkat.oculusbot.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.github.jrmeschkat.oculusbot.constants.NetworkCommunicationsConstants;
import com.github.jrmeschkat.oculusbot.server.clienthandling.SendFramesTask;


public class StartServer {
	public static final int PORT = 1337;

	public static void main(String[] args) {
		StartServer server = new StartServer();
		server.start();

	}
	
	private DatagramSocket socket = null;
	
	public StartServer(){
		try {
			socket = new DatagramSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not open server socket on port "+PORT+".");
			e.printStackTrace();
		}
		
	}
	
	
	public void start() {
		while(true){
			try {
				if(socket != null){
					byte[] buf = new byte[1024];
					DatagramPacket p = new DatagramPacket(buf, buf.length);
					socket.receive(p);
					String msg = new String(p.getData()).trim();
					if(msg.equals(NetworkCommunicationsConstants.REQUEST_CAMERA_DATA)){
						System.out.println("Recieved request from "+p.getAddress().getHostAddress()+": "+msg);
						Thread client = new Thread(new SendFramesTask(socket, p.getAddress(), p.getPort()));
						client.start();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
