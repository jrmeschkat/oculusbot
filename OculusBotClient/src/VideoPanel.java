import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class VideoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage current;
	private int FPS = 30;

	public BufferedImage getCurrent() {
		return current;
	}

	public void setCurrent(BufferedImage current) {
		this.current = current;
	}

	public VideoPanel(LinkedList<Frame> frames) {

		new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(
				new DrawFrameTask(this, frames), 0, 1000 / FPS,
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(current, null, 0, 0);
	}

	private class DrawFrameTask implements Runnable {
		private VideoPanel canvas;
		private LinkedList<Frame> frames;

		public DrawFrameTask(VideoPanel canvas, LinkedList<Frame> frames) {
			this.canvas = canvas;
			this.frames = frames;
		}

		@Override
		public void run() {
			try {
				Frame frame = frames.removeLast();
				System.out.println("Time: "+(System.currentTimeMillis()-frame.getTimestamp()));
				BufferedImage img = matToBufferedImage(frame.getFrame());
				canvas.setCurrent(img);
				canvas.repaint();
			} catch (NoSuchElementException e) {
			}
		}

	}

	public BufferedImage matToBufferedImage(MatOfByte matBGR) {
		// int width = matBGR.width(), height = matBGR.height(), channels =
		// matBGR.channels() ;
		// byte[] sourcePixels = new byte[width * height * channels];
		// matBGR.get(0, 0, sourcePixels);
		// // create new image and get reference to backing data
		// BufferedImage image = new BufferedImage(width, height,
		// BufferedImage.TYPE_3BYTE_BGR);
		// final byte[] targetPixels = ((DataBufferByte)
		// image.getRaster().getDataBuffer()).getData();
		// System.arraycopy(sourcePixels, 0, targetPixels, 0,
		// sourcePixels.length);
		BufferedImage result = null;
		try {
			result = ImageIO.read(new ByteArrayInputStream(matBGR.toArray()));
		} catch (IOException e) {
		}
		return result;
	}
}
