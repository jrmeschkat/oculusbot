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
	private int FPS = 15;
	private int posX;
	private int posY;

	public BufferedImage getCurrent() {
		return current;
	}

	public void setCurrent(BufferedImage current) {
		this.current = current;
	}

	public VideoPanel(LinkedList<Frame> frames) {
		this(frames, 0, 0);
	}
	
	public VideoPanel(LinkedList<Frame> frames, int posX, int posY) {
		new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay( new DrawFrameTask(this, frames), 0, 1000 / FPS, TimeUnit.MILLISECONDS);
		this.posX = posX;
		this.posY = posY;
	}
	

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(current, null, posX, posY);
	}
}
