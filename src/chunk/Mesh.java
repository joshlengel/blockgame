package chunk;

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

public class Mesh {

	private static final int[] V_LEFT = {
			0, 0, 1,
			0, 0, 0,
			0, 1, 0,
			0, 1, 1
	};
	
	private static final int[] V_RIGHT = {
			1, 0, 0,
			1, 0, 1,
			1, 1, 1,
			1, 1, 0
	};
	
	private static final int[] V_DOWN = {
			0, 0, 1,
			1, 0, 1,
			1, 0, 0,
			0, 0, 0
	};
	
	private static final int[] V_UP = {
			0, 1, 0,
			1, 1, 0,
			1, 1, 1,
			0, 1, 1
	};
	
	private static final int[] V_FRONT = {
			0, 0, 0,
			1, 0, 0,
			1, 1, 0,
			0, 1, 0
	};
	
	private static final int[] V_BACK = {
			1, 0, 1,
			0, 0, 1,
			0, 1, 1,
			1, 1, 1
	};
	
	private static final float EPSILON = 0.001f;
	
	private int vaoID;
	private int vBufferID;
	private int tBufferID;
	private int lBufferID;
	private int iBufferID;
	
	private List<Integer> vertices;
	private List<Float> textureCoords;
	private List<Float> lightingValues;
	private List<Integer> indices;
	
	private int vertexCount;
	
	public Mesh() {
		vaoID = GL30.glGenVertexArrays();
		vBufferID = GL15.glGenBuffers();
		tBufferID = GL15.glGenBuffers();
		lBufferID = GL15.glGenBuffers();
		iBufferID = GL15.glGenBuffers();
		
		vertices = new ArrayList<Integer>();
		textureCoords = new ArrayList<Float>();
		lightingValues = new ArrayList<Float>();
		indices = new ArrayList<Integer>();
	}
	
	public void addBlockFace(int x, int y, int z, Face face, BlockData data) {
		int[] faceVertices;
		Vector2f faceTextureCoords;
		float lightingValue;
		
		switch (face) {
		case LEFT:
			faceVertices = V_LEFT;
			faceTextureCoords = data.texCoords[1];
			lightingValue = Constants.LIGHT_RL;
			break;
			
		case RIGHT:
			faceVertices = V_RIGHT;
			faceTextureCoords = data.texCoords[1];
			lightingValue = Constants.LIGHT_RL;
			break;
			
		case DOWN:
			faceVertices = V_DOWN;
			faceTextureCoords = data.texCoords[0];
			lightingValue = Constants.LIGHT_DOWN;
			break;
			
		case UP:
			faceVertices = V_UP;
			faceTextureCoords = data.texCoords[2];
			lightingValue = Constants.LIGHT_UP;
			break;
			
		case FRONT:
			faceVertices = V_FRONT;
			faceTextureCoords = data.texCoords[1];
			lightingValue = Constants.LIGHT_FB;
			break;
			
		case BACK:
			faceVertices = V_BACK;
			faceTextureCoords = data.texCoords[1];
			lightingValue = Constants.LIGHT_FB;
			break;
			
		default:
			throw new IllegalStateException("Face value " + face + " unknown or unpermitted");
		}
		
		int vertexPointer = vertices.size() / 3;
		
		for (int i = 0; i < 4; i++) {
			vertices.add(faceVertices[i * 3    ] + x);
			vertices.add(faceVertices[i * 3 + 1] + y);
			vertices.add(faceVertices[i * 3 + 2] + z);
			
			lightingValues.add(lightingValue);
		}
		
		textureCoords.add(faceTextureCoords.x + EPSILON);
		textureCoords.add(faceTextureCoords.y + Constants.BLOCK_TEXTURE.getUnitHeight() - EPSILON);
		
		textureCoords.add(faceTextureCoords.x + Constants.BLOCK_TEXTURE.getUnitWidth() - EPSILON);
		textureCoords.add(faceTextureCoords.y + Constants.BLOCK_TEXTURE.getUnitHeight() - EPSILON);
		
		textureCoords.add(faceTextureCoords.x + Constants.BLOCK_TEXTURE.getUnitWidth() - EPSILON);
		textureCoords.add(faceTextureCoords.y + EPSILON);
		
		textureCoords.add(faceTextureCoords.x + EPSILON);
		textureCoords.add(faceTextureCoords.y + EPSILON);
		
		indices.add(vertexPointer    );
		indices.add(vertexPointer + 1);
		indices.add(vertexPointer + 2);
		
		indices.add(vertexPointer    );
		indices.add(vertexPointer + 2);
		indices.add(vertexPointer + 3);
	}
	
	public void generate() {
		IntBuffer vBuffer = BufferUtils.createIntBuffer(vertices.size());
		for (int i : vertices)
			vBuffer.put(i);
		
		vBuffer.flip();
		
		FloatBuffer tBuffer = BufferUtils.createFloatBuffer(textureCoords.size());
		for (float f : textureCoords)
			tBuffer.put(f);
		
		tBuffer.flip();
		
		FloatBuffer lBuffer = BufferUtils.createFloatBuffer(lightingValues.size());
		for (float f : lightingValues)
			lBuffer.put(f);
		
		lBuffer.flip();
		
		IntBuffer iBuffer = BufferUtils.createIntBuffer(indices.size());
		for (int i : indices)
			iBuffer.put(i);
		
		iBuffer.flip();
		
		GL30.glBindVertexArray(vaoID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_INT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 0, 0l);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		
		vertexCount = indices.size();
	}
	
	public void clear() {
		vertices.clear();
		textureCoords.clear();
		lightingValues.clear();
		indices.clear();
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public void free() {
		GL15.glDeleteBuffers(vBufferID);
		GL15.glDeleteBuffers(tBufferID);
		GL15.glDeleteBuffers(lBufferID);
		GL15.glDeleteBuffers(iBufferID);
		GL30.glDeleteVertexArrays(vaoID);
	}
}
