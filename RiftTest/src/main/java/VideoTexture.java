import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import static org.lwjgl.opengl.GL11.*;

public class VideoTexture {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private VideoCapture cam;
	private Mat frame;
	private ByteBuffer buffer;

	public VideoTexture() {
		cam = new VideoCapture();
		cam.open(0);

		if (!cam.isOpened()) {
			throw new IllegalStateException();
		}
	}

	public int grabVideoFrameTexture() {
		frame = new Mat();
		cam.read(frame);
//		Core.flip(frame, frame, 0);

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

	public void release() {
		try {
			cam.release();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}
