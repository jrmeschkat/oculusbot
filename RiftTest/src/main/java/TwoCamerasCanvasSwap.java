import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.ovr.OVR.ovrEye_Left;
import static org.lwjgl.ovr.OVR.ovrEye_Right;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import oculusbot.opengl.Renderable;
import oculusbot.opengl.ShaderUtils;

public class TwoCamerasCanvasSwap implements Renderable {
	private int bufferLeft;
	private int bufferRight;
	private int program;
	private int texture;
	private float[] verticesLeft;
	private float[] verticesRight;
	private VideoTexture2Cams videoTexture;
	private int eye;

	public TwoCamerasCanvasSwap() {
		videoTexture = new VideoTexture2Cams();
	}

	public void destroy() {
		videoTexture.release();
	}

	public void init() {
		//create shader program
		try {
			program = ShaderUtils.createShaderProgram("texture.vert", "texture.frag");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//create shape data
		verticesLeft = new float[] { 1f, -1f, 0.5f, 1, -1f, -1f, 0, 1, 1f, 1f, 0.5f, 0, -1f, 1f, 0, 0 };
		FloatBuffer canvasLeft = BufferUtils.createFloatBuffer(verticesLeft.length);
		canvasLeft.put(verticesLeft).flip();

		verticesRight = new float[] { 1f, -1f, 1, 1, -1f, -1f, 0.5f, 1, 1f, 1f, 1, 0, -1f, 1f, 0.5f, 0 };
		FloatBuffer canvasRight = BufferUtils.createFloatBuffer(verticesRight.length);
		canvasRight.put(verticesRight).flip();

		//load shape data
		bufferLeft = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferLeft);
		glBufferData(GL_ARRAY_BUFFER, canvasLeft, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		bufferRight = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferRight);
		glBufferData(GL_ARRAY_BUFFER, canvasRight, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public void render() {
		texture = videoTexture.grabVideoFrameTexture();

		if (eye == ovrEye_Left) {
			glBindBuffer(GL_ARRAY_BUFFER, bufferLeft);
		} else if (eye == ovrEye_Right) {
			glBindBuffer(GL_ARRAY_BUFFER, bufferRight);
		} else {
			throw new IllegalStateException("TwoCameraCanvas: eye-value not allowed: " + eye);
		}
		glUseProgram(program);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		int textureSampler = glGetUniformLocation(program, "textureSampler");
		glUniform1i(textureSampler, 0);

		int position = glGetAttribLocation(program, "position");
		glEnableVertexAttribArray(position);
		glVertexAttribPointer(position, 2, GL_FLOAT, false, verticesLeft.length, 0);

		int vertexUV = glGetAttribLocation(program, "vertexUV");
		glEnableVertexAttribArray(vertexUV);
		glVertexAttribPointer(vertexUV, 2, GL_FLOAT, false, verticesLeft.length, 8);

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		glDisableVertexAttribArray(0);
	}

	public void setEye(int eye) {
		this.eye = eye;
	}
	
	public int getEye(){
		return eye;
	}

}
