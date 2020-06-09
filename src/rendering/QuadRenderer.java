package rendering;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import data.Texture;
import window.Display;

public class QuadRenderer {

	private static int vaoID;
	private static int vBufferID;
	private static int tBufferID;
	private static int iBufferID;
	
	static {
		vaoID = GL30.glGenVertexArrays();
		vBufferID = GL15.glGenBuffers();
		tBufferID = GL15.glGenBuffers();
		iBufferID = GL15.glGenBuffers();
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new int[] {-1, -1, 1, -1, 1, 1, -1, 1}, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_INT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new int[] {0, 1, 1, 1, 1, 0, 0, 0}, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_INT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, new int[] {0, 1, 2, 0, 2, 3}, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	private Shader shader;
	
	public static class Quad {
		Texture texture;
		
		Vector2f offset;
		float scale;
	}
	
	public QuadRenderer() {
		shader = new Shader("quadVertexShader.txt", "quadFragmentShader.txt");
		shader.setUniforms("aspectRatio", "offset", "scale");
	}
	
	public void render(Display display, Quad quad) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		shader.activate();
		shader.loadUniform("aspectRatio", display.getAspectRatio());
		shader.loadUniform("offset", quad.offset);
		shader.loadUniform("scale", quad.scale);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, quad.texture.getTextureID());
		
		GL30.glBindVertexArray(vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0l);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		
		shader.deactivate();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void free() {
		shader.free();
	}
	
	public static void freeStatic() {
		GL30.glDeleteVertexArrays(vaoID);
		GL15.glDeleteBuffers(vBufferID);
		GL15.glDeleteBuffers(tBufferID);
		GL15.glDeleteBuffers(iBufferID);
	}
}
