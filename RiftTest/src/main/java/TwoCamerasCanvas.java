import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import oculusbot.opengl.Renderable;
import oculusbot.opengl.ShaderUtils;

public class TwoCamerasCanvas implements Renderable {
	private int buffer;
	private int program;
	private int texture;
	private float[] cords;
	private VideoTexture2Cams videoTexture;

	public TwoCamerasCanvas() {
		videoTexture = new VideoTexture2Cams();
	}

	public void destroy() {
		videoTexture.release();
		//TODO unload scene
	}

	public void init() {
		//create shader program
		try {
			program = ShaderUtils.createShaderProgram("texture.vert", "texture.frag");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//create shape data
		cords = new float[] { 1f, -1f, 1, 1, -1f, -1f, 0, 1, 1f, 1f, 1, 0, -1f, 1f, 0, 0 };
		FloatBuffer shape = BufferUtils.createFloatBuffer(cords.length);
		shape.put(cords).flip();

		//load shape data
		buffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		glBufferData(GL_ARRAY_BUFFER, shape, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public void render() {
		texture = videoTexture.grabVideoFrameTexture();
		glClear(GL_COLOR_BUFFER_BIT);
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		glUseProgram(program);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		int textureSampler = glGetUniformLocation(program, "textureSampler");
		glUniform1i(textureSampler, 0);

		int position = glGetAttribLocation(program, "position");
		glEnableVertexAttribArray(position);
		glVertexAttribPointer(position, 2, GL_FLOAT, false, cords.length, 0);

		int vertexUV = glGetAttribLocation(program, "vertexUV");
		glEnableVertexAttribArray(vertexUV);
		glVertexAttribPointer(vertexUV, 2, GL_FLOAT, false, cords.length, 8);

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		glDisableVertexAttribArray(0);
	}

}
