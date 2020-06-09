package rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import interactive.text.Text;
import window.Display;

public class TextRenderer {

	private Shader shader;
	
	public TextRenderer() {
		shader = new Shader("textVertexSHader.txt", "textFragmentShader.txt");
		shader.setUniforms("aspectRatio", "color");
	}
	
	public void render(Display display, Text t) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		shader.activate();
		shader.loadUniform("aspectRatio", display.getAspectRatio());
		shader.loadUniform("color", t.getColor());
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, t.getMesh().getFont().getTexture().getTextureID());
		
		GL30.glBindVertexArray(t.getMesh().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, t.getMesh().getVertexCount(), GL11.GL_UNSIGNED_INT, 0l);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		
		shader.deactivate();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public void free() {
		shader.free();
	}
}
