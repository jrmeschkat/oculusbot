import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;



public class VideoPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private VideoCapture capture;
	private BufferedImage current;
	private int FPS = 30;
	
	public BufferedImage getCurrent() {	return current;	}
	public void setCurrent(BufferedImage current) {	this.current = current;	}

	public VideoPanel(VideoCapture capture){
		this.capture = capture;
		
		Runnable captureFrameTask = new CaptureFrameTask(this);
		new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(captureFrameTask, 0, 1000/FPS, TimeUnit.MILLISECONDS);
	}
	
	public void stop(){
		capture.release();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(current, null, 0, 0);
	}
	
	private class CaptureFrameTask implements Runnable{
		private VideoPanel canvas;
		
		public CaptureFrameTask(VideoPanel canvas){
			this.canvas = canvas;
		}
		
		@Override
		public void run() {
			Mat frame = new Mat();
			capture.read(frame);
			BufferedImage img = null;
			try {
				img = convertToBufferedImage(frame);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			canvas.setCurrent(img);
			canvas.repaint();
		}
		
	}
	
	public static BufferedImage convertToBufferedImage(Mat image)
			throws IOException {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		BufferedImage result = ImageIO.read(new ByteArrayInputStream(bytemat
				.toArray()));
		return result;
	}
}
