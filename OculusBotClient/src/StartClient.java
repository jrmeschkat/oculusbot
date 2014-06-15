import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class StartClient extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final String HOST = "localhost";
	public static final int PORT = 1337;
	
	private Socket socket = null;
	private LinkedList<Mat> frames = new LinkedList<>();
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public static void main(String[] args) {
		new StartClient();
	}
	
	public StartClient(){
		try {
			socket = new Socket(HOST, PORT);
			Thread recieveFramesThread = new Thread(new ReceiveFramesTask(socket, frames));
			recieveFramesThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.addWindowListener(new WindowClosingListner() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				if(socket != null){
					try { socket.close(); } catch (IOException e1) {}
				}
			}
		});
		
		this.setSize(800, 600);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container pane = this.getContentPane();
		VideoPanel panel = new VideoPanel(frames);
		pane.add(panel);
		this.setVisible(true);

	}

}
