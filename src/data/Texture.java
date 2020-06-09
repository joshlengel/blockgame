package data;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

public class Texture {

	private int atlasWidth;
	private int atlasHeight;
	private float unitWidth;
	private float unitHeight;
	
	private int textureID;
	
	public Texture(String textureName, int atlasWidth, int atlasHeight) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		
		unitWidth = 1.0f / atlasWidth;
		unitHeight = 1.0f / atlasHeight;
		
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		int[] width = new int[1];
		int[] height = new int[1];
		int[] comp = new int[1];
		
		ByteBuffer pixels = STBImage.stbi_load("res/" + textureName, width, height, comp, 4);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width[0], height[0], 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	}
	
	public int getAtlasWidth() {
		return atlasWidth;
	}
	
	public int getAtlasHeight() {
		return atlasHeight;
	}
	
	public float getUnitWidth() {
		return unitWidth;
	}
	
	public float getUnitHeight() {
		return unitHeight;
	}
	
	public int getTextureID() {
		return textureID;
	}
	
	public void free() {
		GL11.glDeleteTextures(textureID);
	}
}
