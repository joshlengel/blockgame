package rendering;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import chunk.Chunk;
import chunk.ChunkScape;
import chunk.Mesh;
import data.Constants;
import interactive.Camera;
import window.Display;

public class ChunkRenderer {

	private Shader shader;
	
	public ChunkRenderer() {
		shader = new Shader("meshVertexShader.txt", "meshFragmentShader.txt");
		shader.setUniforms("projectionView", "cameraPosition", "skyColor");
	}
	
	public void render(Display display, Camera camera, ChunkScape scape) {
		shader.activate();
		
		Matrix4f projectionView = new Matrix4f();
		display.getProjectionMatrix().mul(camera.getViewMatrix(), projectionView);
		
		shader.loadUniform("projectionView", projectionView);
		shader.loadUniform("cameraPosition", camera.getPosition());
		shader.loadUniform("skyColor", display.getBackgroundColor());
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Constants.BLOCK_TEXTURE.getTextureID());
		
		for (int xIndex = -scape.getRenderDistance(); xIndex <= scape.getRenderDistance(); xIndex++) {
			for (int zIndex = -scape.getRenderDistance(); zIndex <= scape.getRenderDistance(); zIndex++) {
				for (int yIndex = 0; yIndex < Chunk.STACK_HEIGHT; yIndex++) {
					Mesh mesh = scape.getChunk(xIndex, zIndex).getSubchunk(yIndex).getMesh();
					
					GL30.glBindVertexArray(mesh.getVaoID());
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(1);
					GL20.glEnableVertexAttribArray(2);
					
					GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0l);
					
					GL20.glDisableVertexAttribArray(0);
					GL20.glDisableVertexAttribArray(1);
					GL20.glDisableVertexAttribArray(2);
				}
			}
		}
		
		GL30.glBindVertexArray(0);
		
		shader.deactivate();
	}
	
	public void free() {
		shader.free();
	}
}
