package com.github.jrmeschkat.oculusbot.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.github.jrmeschkat.oculusbot.constants.NetworkCommunicationsConstants;
import com.github.jrmeschkat.oculusbot.server.clienthandling.ClientHandler;
import com.github.jrmeschkat.oculusbot.server.clienthandling.SendFramesThread;


public class StartServer {
	public static final int PORT = 1337;
	
	private ClientHandler client;
	private DatagramSocket leftSocket = null;
	private DatagramSocket rightSocket = null;

	public static void main(String[] args) {
		StartServer server = new StartServer();
		server.start();

	}
	
	
	public StartServer(){
		try {
			leftSocket = new DatagramSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not open left server socket on port "+PORT+".");
			e.printStackTrace();
		}
		try {
			rightSocket = new DatagramSocket(PORT+1);
		} catch (SocketException e) {
			System.err.println("Could not open right server socket on port "+PORT+".");
			e.printStackTrace();
		}
		
	}
	
	
	public void start() {
		System.out.println("Started server on port: "+PORT+" and "+(PORT+1));
		while(true){
			try {
				if(leftSocket != null){
					byte[] buf = new byte[1024];
					DatagramPacket p = new DatagramPacket(buf, buf.length);
					leftSocket.receive(p);
					String msg = new String(p.getData()).trim();
					System.out.println("Recieved request from "+p.getAddress().getHostAddress()+": "+msg);
					
					//start client thread
					if(msg.equals(NetworkCommunicationsConstants.REQUEST_CAMERA_DATA)){
						if(client == null || !client.isAlive()){
							client = new ClientHandler(leftSocket, rightSocket, p.getAddress(), p.getPort());
						}
					}
					
					if(msg.equals(NetworkCommunicationsConstants.END_CONNECTION)){
						if(client != null){
							client.interrupt();
							byte[] data = NetworkCommunicationsConstants.DONE.getBytes();
							DatagramPacket answer = new DatagramPacket(data, data.length, p.getAddress(), p.getPort());
							leftSocket.send(answer);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
