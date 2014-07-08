import java.awt.Container;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.opencv.core.Core;

import com.github.jrmeschkat.oculusbot.constants.NetworkCommunicationsConstants;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;

public class StartClient extends JFrame {
	private static final long serialVersionUID = 1L;
	// TODO find server on network
	public static final String HOST = "localhost";
	// public static final String HOST = "192.168.178.39";
	public static final int PORT = 1337;

	private ReceiveFramesTask receiveFramesTask = null;

	private DatagramSocket socket = null;
	private LinkedList<Frame> framesLeft = new LinkedList<>();
	private LinkedList<Frame> framesRight = new LinkedList<>();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		new StartClient();
	}

	public StartClient() {

		try {
			sendMsg(NetworkCommunicationsConstants.REQUEST_CAMERA_DATA);
			receiveFramesTask = new ReceiveFramesTask(socket, framesLeft, framesRight);
			Thread recieveFramesThread = new Thread(receiveFramesTask);
			recieveFramesThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.addWindowListener(new WindowClosingListner() {

			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("CLOSING WINDOW");
				receiveFramesTask.interrupt();

				try {
					sendMsg(NetworkCommunicationsConstants.END_CONNECTION);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// FIXME wait for "DONE"
				if (socket != null) {
					socket.close();
				}
			}

		});

		this.setSize(1600, 600);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		VideoPanel panelLeft = new VideoPanel(framesLeft);
		getContentPane().add(panelLeft);
		VideoPanel panelRight = new VideoPanel(framesRight);
		getContentPane().add(panelRight);
		
		this.setVisible(true);

	}

	private void sendMsg(String msg) throws UnknownHostException, IOException {
		InetAddress add = InetAddress.getByName(HOST);
		socket = new DatagramSocket();
		byte[] data = msg.getBytes();
		DatagramPacket p = new DatagramPacket(data, data.length, add, PORT);
		socket.send(p);
	}

}
