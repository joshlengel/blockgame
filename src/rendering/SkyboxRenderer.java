package rendering;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import data.Constants;
import interactive.Camera;
import window.Display;

public class SkyboxRenderer {

	private static final int SIZE = 170;
	
	private static final int[] VERTICES = {
			-SIZE, -SIZE, -SIZE,
			 SIZE, -SIZE, -SIZE,
			 SIZE,  SIZE, -SIZE,
			-SIZE,  SIZE, -SIZE,
			-SIZE, -SIZE,  SIZE,
			 SIZE, -SIZE,  SIZE,
			 SIZE,  SIZE,  SIZE,
			-SIZE,  SIZE,  SIZE
	};
	
	private static final int[] INDICES = {
			1, 0, 3,
		    1, 3, 2,
		    4, 5, 6,
		    4, 6, 7,
		    5, 1, 2,
		    5, 2, 6,
		    0, 4, 7,
		    0, 7, 3,
		    2, 3, 7,
		    2, 7, 6,
		    5, 4, 0,
		    5, 0, 1
	};
	
	private int vaoID;
	private int vBufferID;
	private int iBufferID;
	
	private Shader shader;
	
	public SkyboxRenderer() {
		shader = new Shader("skyboxVertexShader.txt", "skyboxFragmentShader.txt");
		shader.setUniforms("projectionView", "skyColor");
		
		vaoID = GL30.glGenVertexArrays();
		vBufferID = GL15.glGenBuffers();
		iBufferID = GL15.glGenBuffers();
		
		IntBuffer vBuffer = BufferUtils.createIntBuffer(24);
		vBuffer.put(VERTICES);
		vBuffer.flip();
		
		IntBuffer iBuffer = BufferUtils.createIntBuffer(36);
		iBuffer.put(INDICES);
		iBuffer.flip();
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_INT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void render(Display display, Camera camera) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		shader.activate();
		
		Matrix4f projectionView = new Matrix4f();
		display.getProjectionMatrix().mul(camera.getViewMatrix(), projectionView).translate(camera.getPosition());
		
		shader.loadUniform("projectionView", projectionView);
		shader.loadUniform("skyColor", display.getBackgroundColor());
		
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, Constants.SKYBOX_TEXTURE.getTextureID());
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0l);
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		shader.deactivate();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void free() {
		GL15.glDeleteBuffers(vBufferID);
		GL15.glDeleteBuffers(iBufferID);
		GL30.glDeleteVertexArrays(vaoID);
		
		shader.free();
	}
}
