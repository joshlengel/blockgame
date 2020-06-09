package rendering;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import data.Constants;
import window.Display;

public class CrosshairsRenderer {

	private static final int[] VERTICES = {
			-1, -1,
			 1, -1,
			 1,  1,
			-1,  1
	};
	
	private static final int[] INDICES = {
			0, 1, 2,
			0, 2, 3
	};
	
	private int vaoID;
	private int vBufferID;
	private int iBufferID;
	
	private Shader shader;
	
	public CrosshairsRenderer() {
		shader = new Shader("crosshairsVertexShader.txt", "crosshairsFragmentShader.txt");
		shader.setUniforms("aspectRatio");
		
		vaoID = GL30.glGenVertexArrays();
		vBufferID = GL15.glGenBuffers();
		iBufferID = GL15.glGenBuffers();
		
		IntBuffer vBuffer = BufferUtils.createIntBuffer(8);
		vBuffer.put(VERTICES);
		vBuffer.flip();
		
		IntBuffer iBuffer = BufferUtils.createIntBuffer(6);
		iBuffer.put(INDICES);
		iBuffer.flip();
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_INT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void render(Display display) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		shader.activate();
		shader.loadUniform("aspectRatio", display.getAspectRatio());
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Constants.CROSSHAIRS_TEXTURE.getTextureID());
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0l);
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		shader.deactivate();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public void free() {
		GL15.glDeleteBuffers(vBufferID);
		GL15.glDeleteBuffers(iBufferID);
		GL30.glDeleteVertexArrays(vaoID);
		
		shader.free();
	}
}
