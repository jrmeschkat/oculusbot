import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

import org.opencv.core.MatOfByte;

public class DrawFrameTask implements Runnable {
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
			// if(System.currentTimeMillis() - frame.getTimestamp() > FRAMEDROPLIMIT){
			// return;
			// } 
			BufferedImage img = matToBufferedImage(frame.getFrame());
			canvas.setCurrent(img);
			canvas.repaint();
		} catch (NoSuchElementException e) {
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
		} catch (IOException e) {}
		return result;
	}

}
