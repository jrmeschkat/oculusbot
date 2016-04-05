import java.nio.ByteBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import static org.lwjgl.opengl.GL11.*;

public class VideoTexture2Cams {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private VideoCapture camLeft;
	private VideoCapture camRight;
	private Mat frame;
	private ByteBuffer buffer;
	private int width;
	private int height;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public VideoTexture2Cams() {
		camLeft = new VideoCapture();
		camRight = new VideoCapture();
		camLeft.open(0);
		camRight.open(1);

		if (!camLeft.isOpened()) {
			throw new IllegalStateException("Couldn't open left cam.");
		}
		if (!camRight.isOpened()) {
			throw new IllegalStateException("Couldn't open right cam.");
		}

		Mat m = grabFrame();
		width = m.cols();
		height = m.rows();
	}

	public int grabVideoFrameTexture() {
		frame = grabFrame();

		int size = frame.rows() * frame.cols() * 4;
		buffer = BufferUtils.createByteBuffer(size);
		byte[] data = new byte[size];
		frame.get(0, 0, data);
		buffer.put(data).flip();

		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
		glPixelStorei(GL_UNPACK_ROW_LENGTH, (int) (frame.step1() / frame.elemSize()));
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, frame.cols(), frame.rows(), 0, GL12.GL_BGR, GL_UNSIGNED_BYTE, buffer);

		return texture;
	}

	private Mat grabFrame() {
		Mat frame = new Mat();
		Mat left = new Mat();
		Mat right = new Mat();

		camLeft.read(left);
		camRight.read(right);

		Core.hconcat(Arrays.asList(new Mat[] { left, right }), frame);

		return frame;
	}

	public void release() {
		try {
			camLeft.release();
			camRight.release();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}
