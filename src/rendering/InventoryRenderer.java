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
					amounts[i] = new Text(x, -0.75f, font, 0.25f);
				
				amounts[i].setText(String.valueOf(inventory.getItems(i)));
				
				int vertexPointer = vertices.size() / 2;
				
				vertices.add(x);
				vertices.add(-1.0f);
				
				vertices.add(x + 0.25f);
				vertices.add(-1.0f);
				
				vertices.add(x + 0.25f);
				vertices.add(-0.75f);
				
				vertices.add(x);
				vertices.add(-0.75f);
				
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
		
		GL30.glDeleteVertexArrays(iVaoID);
		GL15.glDeleteBuffers(iVBufferID);
		GL15.glDeleteBuffers(iTBufferID);
		GL15.glDeleteBuffers(iIBufferID);
	}
}
