import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;

public class HelloOpenCV extends JFrame {

	private static final long serialVersionUID = 1L;
	private VideoPanel panel;

	public static void main(String[] args) {
		new HelloOpenCV();
	}
	
	public HelloOpenCV(){

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoCapture capture = new VideoCapture(0);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container pane = this.getContentPane();
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				panel.stop();
			}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		panel = new VideoPanel(capture);
		pane.add(panel);
		this.setVisible(true);


	}

}
