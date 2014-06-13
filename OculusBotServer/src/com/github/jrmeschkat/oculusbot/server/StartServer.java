package com.github.jrmeschkat.oculusbot.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.github.jrmeschkat.oculusbot.server.clienthandling.HandleClientTask;


public class StartServer {
	public static final int PORT = 1337;

	public static void main(String[] args) {
		StartServer server = new StartServer();
		server.start();

	}
	
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	public StartServer(){
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not open server socket on port "+PORT+".");
			e.printStackTrace();
		}
		
	}
	
	
	public void start() {
		while(true){
			try {
				if(serverSocket != null){
					socket = serverSocket.accept();
				}
				Thread client = new Thread(new HandleClientTask(socket));
				client.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
