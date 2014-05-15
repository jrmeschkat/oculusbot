package de.unikassel.meschkat.robert;
//import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
//import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
//import com.github.sarxos.webcam.WebcamPanel;

//An example from webcam-capture git

public class WebcamPanelExample {

	public static void main(String[] args) 
//			throws InterruptedException 
	{
		System.out.println("get webcam");
		Webcam webcam = Webcam.getDefault();
		System.out.println("opening webcam");
		webcam.open();
		System.out.println("try getting image");
		try {
			ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));
			System.out.println("got image");
		} catch (IOException e) {
			e.printStackTrace();
		}
//		Webcam webcam = Webcam.getDefault();
//		webcam.setViewSize(new Dimension(640, 480));
//		WebcamPanel panel = new WebcamPanel(webcam);
//		panel.setFPSDisplayed(true);
//
//		JFrame window = new JFrame("Test webcam panel");
//		window.add(panel);
//		window.setResizable(true);
//		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		window.pack();
//		window.setVisible(true);
	}
}
