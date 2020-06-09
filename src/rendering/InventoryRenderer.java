package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import data.BlockData;
import data.Constants;
import interactive.inventory.Inventory;
import interactive.text.Font;
import interactive.text.Text;
import rendering.QuadRenderer.Quad;
import window.Display;

public class InventoryRenderer {

	private QuadRenderer qr;
	private TextRenderer tr;
	
	private Inventory inventory;
	
	private Quad bar;
	private Quad selector;
	
	private Text[] amounts;
	
	private int iVaoID;
	private int iVBufferID;
	private int iTBufferID;
	private int iIBufferID;
	
	private int iIndexCount;
	
	private Shader quadShader;
	
	public InventoryRenderer(Inventory inventory) {
		qr = new QuadRenderer();
		tr = new TextRenderer();
		
		bar = new Quad();
		bar.texture = Constants.INVENTORY_BAR_TEXTURE;
		bar.scale = 1;
		bar.offset = new Vector2f(0.0f, -1.75f);
		
		selector = new Quad();
		selector.texture = Constants.INVENTORY_BAR_SELECTOR_TEXTURE;
		selector.scale = 1;
		selector.offset = new Vector2f(0.0f, -1.75f);
		
		amounts = new Text[Inventory.SLOTS];
		
		iVaoID = GL30.glGenVertexArrays();
		iVBufferID = GL15.glGenBuffers();
		iTBufferID = GL15.glGenBuffers();
		iIBufferID = GL15.glGenBuffers();
		
		this.inventory = inventory;
		
		quadShader = new Shader("inventoryVertexShader.txt", "inventoryFragmentShader.txt");
		quadShader.setUniforms("aspectRatio");
	}
	
	public void update(Font font) {
		selector.offset.x = inventory.getSelectedSlot() * 0.25f;
		
		List<Float> vertices = new ArrayList<Float>();
		List<Float> textureCoords = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();
		
		for (int i = 0; i < Inventory.SLOTS; i++) {
			if (inventory.available(i)) {
				float x = i * 0.25f - 1;
				
				BlockData b = Constants.getByName(inventory.getItem(i));
				float texX = b.texCoords[1].x;
				float texY = b.texCoords[1].y;
				
				if (amounts[i] == null)
					amounts[i] = new Text(x + 0.18f, -0.9f, font, 0.25f);
				
				amounts[i].setText(String.valueOf(inventory.getItems(i)));
				
				int vertexPointer = vertices.size() / 2;
				
				vertices.add(x + 0.05f);
				vertices.add(-0.95f);
				
				vertices.add(x + 0.2f);
				vertices.add(-0.95f);
				
				vertices.add(x + 0.2f);
				vertices.add(-0.8f);
				
				vertices.add(x + 0.05f);
				vertices.add(-0.8f);
				
				textureCoords.add(texX);
				textureCoords.add(texY + Constants.BLOCK_TEXTURE.getUnitHeight());
				
				textureCoords.add(texX + Constants.BLOCK_TEXTURE.getUnitWidth());
				textureCoords.add(texY + Constants.BLOCK_TEXTURE.getUnitHeight());
				
				textureCoords.add(texX + Constants.BLOCK_TEXTURE.getUnitWidth());
				textureCoords.add(texY);
				
				textureCoords.add(texX);
				textureCoords.add(texY);
				
				indices.add(vertexPointer);
				indices.add(vertexPointer + 1);
				indices.add(vertexPointer + 2);
				
				indices.add(vertexPointer);
				indices.add(vertexPointer + 2);
				indices.add(vertexPointer + 3);
			} else if (amounts[i] != null) {
				amounts[i].free();
				amounts[i] = null;
			}
		}
		
		FloatBuffer vBuffer = BufferUtils.createFloatBuffer(vertices.size());
		for (float f : vertices)
			vBuffer.put(f);
		
		vBuffer.flip();
		
		FloatBuffer tBuffer = BufferUtils.createFloatBuffer(textureCoords.size());
		for (float f : textureCoords)
			tBuffer.put(f);
		
		tBuffer.flip();
		
		IntBuffer iBuffer = BufferUtils.createIntBuffer(indices.size());
		for (int i : indices)
			iBuffer.put(i);
		
		iBuffer.flip();
		
		iIndexCount = indices.size();
		
		GL30.glBindVertexArray(iVaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iVBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iTBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iIBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void render(Display display) {
		qr.render(display, bar);
		qr.render(display, selector);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		quadShader.activate();
		quadShader.loadUniform("aspectRatio", display.getAspectRatio());
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Constants.BLOCK_TEXTURE.getTextureID());
		
		GL30.glBindVertexArray(iVaoID);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, iIndexCount, GL11.GL_UNSIGNED_INT, 0l);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		
		GL30.glBindVertexArray(0);
		
		quadShader.deactivate();
		
		for (Text t : amounts) {
			if (t != null)
				tr.render(display, t);
		}
	}
	
	public void free() {
		qr.free();
		tr.free();
		
		for (Text t : amounts) {
			if (t != null)
				t.free();
		}
		
		quadShader.free();
		
		GL30.glDeleteVertexArrays(iVaoID);
		GL15.glDeleteBuffers(iVBufferID);
		GL15.glDeleteBuffers(iTBufferID);
		GL15.glDeleteBuffers(iIBufferID);
	}
}
