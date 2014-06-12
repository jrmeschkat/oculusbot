import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class ServerTask implements Runnable{

	public static final int PORT = 1337;
	
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	public ServerTask(){
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not open server socket on port "+PORT+".");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		while(true){
			try {
				if(serverSocket != null){
					socket = serverSocket.accept();
				}
				
				new HandleClientTask(socket);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
