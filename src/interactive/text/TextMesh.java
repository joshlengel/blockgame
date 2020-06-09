package interactive.text;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class TextMesh {

	private int vaoID;
	private int vBufferID;
	private int tBufferID;
	private int iBufferID;
	
	private List<Float> vertices = new ArrayList<Float>();
	private List<Float> textureCoords = new ArrayList<Float>();
	private List<Integer> indices = new ArrayList<Integer>();
	
	private float lineLength;
	
	private final float initCursorX, initCursorY;
	private float cursorX, cursorY;
	
	private Font font;
	
	public TextMesh(float x, float y, Font font, float lineLength) {
		this.font = font;
		
		this.initCursorX = x;
		this.initCursorY = y;
		this.cursorX = x;
		this.cursorY = y;
		
		this.lineLength = lineLength;
		
		vaoID = GL30.glGenVertexArrays();
		vBufferID = GL15.glGenBuffers();
		tBufferID = GL15.glGenBuffers();
		iBufferID = GL15.glGenBuffers();
	}
	
	public void setText(String text) {
		vertices.clear();
		textureCoords.clear();
		indices.clear();
		
		cursorX = initCursorX;
		cursorY = initCursorY;
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			Glyph g = font.getGlyph(c);
			
			float x = cursorX + g.getxOffset() * font.getSize();
			float y = cursorY - g.getyOffset() * font.getSize();
			
			float w = g.getWidth() * font.getSize();
			float h = g.getHeight() * font.getSize();
			
			int vertexPointer = vertices.size() / 2;
			
			vertices.add(x);
			vertices.add(y - h);
			
			vertices.add(x + w);
			vertices.add(y - h);
			
			vertices.add(x + w);
			vertices.add(y);
			
			vertices.add(x);
			vertices.add(y);
			
			textureCoords.add(g.getX());
			textureCoords.add(g.getY() + g.getHeight());
			
			textureCoords.add(g.getX() + g.getWidth());
			textureCoords.add(g.getY() + g.getHeight());
			
			textureCoords.add(g.getX() + g.getWidth());
			textureCoords.add(g.getY());
			
			textureCoords.add(g.getX());
			textureCoords.add(g.getY());
			
			indices.add(vertexPointer);
			indices.add(vertexPointer + 1);
			indices.add(vertexPointer + 2);
			
			indices.add(vertexPointer);
			indices.add(vertexPointer + 2);
			indices.add(vertexPointer + 3);
			
			cursorX += g.getxAdvance() * font.getSize();
			
			if (cursorX - initCursorX > lineLength) {
				cursorX = initCursorX;
				cursorY -= font.getLineHeight() * font.getSize();
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
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return indices.size();
	}
	
	public Font getFont() {
		return font;
	}
	
	public void free() {
		GL30.glDeleteVertexArrays(vaoID);
		GL15.glDeleteBuffers(vBufferID);
		GL15.glDeleteBuffers(tBufferID);
		GL15.glDeleteBuffers(iBufferID);
	}
}
