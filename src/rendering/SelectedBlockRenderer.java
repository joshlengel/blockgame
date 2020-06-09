package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import interactive.Camera;
import window.Display;

public class SelectedBlockRenderer {

	private static final float EPSILON = 0.001f;
	
	private static final float[] VERTICES = {
			-EPSILON   , -EPSILON   , -EPSILON,
			1 + EPSILON, -EPSILON   , -EPSILON,
			1 + EPSILON, 1 + EPSILON, -EPSILON,
			-EPSILON   , 1 + EPSILON, -EPSILON,
			-EPSILON   , -EPSILON   , 1 + EPSILON,
			1 + EPSILON, -EPSILON   , 1 + EPSILON,
			1 + EPSILON, 1 + EPSILON, 1 + EPSILON,
			-EPSILON   , 1 + EPSILON, 1 + EPSILON
	};
	
	private static final int[] INDICES = {
			0, 1, 2,
			0, 2, 3,
			5, 4, 7,
			5, 7, 6,
			4, 0, 3,
			4, 3, 7,
			1, 5, 6,
			1, 6, 2,
			3, 2, 6,
			3, 6, 7,
			4, 5, 1,
			4, 1, 0
			
	};
	
	private int vaoID;
	private int vBufferID;
	private int iBufferID;
	
	private Shader shader;
	
	public SelectedBlockRenderer() {
		shader = new Shader("selectedBlockVertexShader.txt", "selectedBlockFragmentShader.txt");
		shader.setUniforms("projectionView", "position");
		
		vaoID = GL30.glGenVertexArrays();
		vBufferID = GL15.glGenBuffers();
		iBufferID = GL15.glGenBuffers();
		
		FloatBuffer vBuffer = BufferUtils.createFloatBuffer(24);
		vBuffer.put(VERTICES);
		vBuffer.flip();
		
		IntBuffer iBuffer = BufferUtils.createIntBuffer(36);
		iBuffer.put(INDICES);
		iBuffer.flip();
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void render(Display display, Camera camera, Vector3i position) {
		if (position == null)
			return;
		
		GL11.glEnable(GL11.GL_BLEND);
		
		shader.activate();
		
		Matrix4f projectionView = new Matrix4f();
		display.getProjectionMatrix().mul(camera.getViewMatrix(), projectionView);
		
		shader.loadUniform("projectionView", projectionView);
		shader.loadUniform("position", position);
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0l);
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		shader.deactivate();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void free() {
		GL15.glDeleteBuffers(vBufferID);
		GL15.glDeleteBuffers(iBufferID);
		GL30.glDeleteVertexArrays(vaoID);
		
		shader.free();
	}
}
