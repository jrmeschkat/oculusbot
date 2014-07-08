package com.github.jrmeschkat.oculusbot.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.github.jrmeschkat.oculusbot.constants.NetworkCommunicationsConstants;
import com.github.jrmeschkat.oculusbot.server.clienthandling.SendFramesThread;


public class StartServer {
	public static final int PORT = 1337;
	
	private Thread client;

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
		System.out.println("Started server on port: "+PORT);
		while(true){
			try {
				if(socket != null){
					byte[] buf = new byte[1024];
					DatagramPacket p = new DatagramPacket(buf, buf.length);
					socket.receive(p);
					String msg = new String(p.getData()).trim();
					System.out.println("Recieved request from "+p.getAddress().getHostAddress()+": "+msg);
					
					//start client thread
					if(msg.equals(NetworkCommunicationsConstants.REQUEST_CAMERA_DATA)){
						if(client == null || !client.isAlive()){
							client = new SendFramesThread(socket, p.getAddress(), p.getPort());
							client.start();
						}
					}
					
					if(msg.equals(NetworkCommunicationsConstants.END_CONNECTION)){
						if(client != null){
							client.interrupt();
							byte[] data = NetworkCommunicationsConstants.DONE.getBytes();
							DatagramPacket answer = new DatagramPacket(data, data.length, p.getAddress(), p.getPort());
							socket.send(answer);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
