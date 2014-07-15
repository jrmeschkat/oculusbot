package com.github.jrmeschkat.oculusbot.server.clienthandling;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientHandler {
	private SendFramesThread left;
	private SendFramesThread right;
	
	public ClientHandler(DatagramSocket leftSocket, DatagramSocket rightSocket, InetAddress clientAddress, int startingPort){
		left = new SendFramesThread(leftSocket, clientAddress, startingPort, 0);
		right = new SendFramesThread(rightSocket, clientAddress, startingPort+1, 1);
		left.start();
		right.start();
	}
	
	public boolean isAlive(){
		if(left != null && right != null)
			return (left.isAlive() && right.isAlive());
		return false;
	}
	
	public void interrupt(){
		left.interrupt();
		right.interrupt();
	}
}
