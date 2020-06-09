package data;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;

public class CubeMap {

	private int textureID;
	
	public CubeMap(String[] textureNames) {
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
		
		for (int i = 0; i < 6; i++) {
			int[] width = new int[1];
			int[] height = new int[1];
			int[] comp = new int[1];
			
			ByteBuffer pixels = STBImage.stbi_load("res/" + textureNames[i], width, height, comp, 4);
			
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, width[0], height[0], 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		}
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    //GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
	    //GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);
	    //GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_R, GL13.GL_CLAMP_TO_EDGE);
	}
	
	public int getTextureID() {
		return textureID;
	}
	
	public void free() {
		GL11.glDeleteTextures(textureID);
	}
}
