package interactive.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import data.Texture;

public class Font {

	private Texture texture;
	
	private float lineHeight;
	private Map<Integer, Glyph> alphabet;
	
	private float size;
	
	public Font(String fontName, float size) {
		File font = new File("res/font/" + fontName + ".fnt");
		
		alphabet = new HashMap<Integer, Glyph>();
		
		this.size = size;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(font))) {
			
			String line;
			
			int scaleW = 0;
			int scaleH = 0;
			
			float invScaleW = 0.0f;
			float invScaleH = 0.0f;
			
			while((line = reader.readLine()) != null) {
				if (line.startsWith("common ")) {
					String[] arguments = line.substring(7).split(" ");
					
					int pixLineHeight = 0;
					
					for (String arg : arguments) {
						String[] value = arg.split("=");
						
						if (value[0].contentEquals("scaleW")) {
							scaleW = Integer.parseInt(value[1]);
							
							invScaleW = 1.0f / scaleW;
							
						} else if (value[0].contentEquals("scaleH")) {
							scaleH = Integer.parseInt(value[1]);
							
							invScaleH = 1.0f / scaleH;
							
						} else if (value[0].contentEquals("lineHeight")) {
							pixLineHeight = Integer.parseInt(value[1]);
						}
					}
					
					lineHeight = pixLineHeight * invScaleH;
					
				} else if (line.startsWith("char ")) {
					String[] parameters = line.substring(5).split(" +");
					
					int ascii = 0;
					int x = 0;
					int y = 0;
					int width = 0;
					int height = 0;
					int xOffset = 0;
					int yOffset = 0;
					int xAdvance = 0;
					
					for (String param : parameters) {
						String[] value = param.split("=");
						
						if (value[0].contentEquals("id")) {
							ascii = Integer.parseInt(value[1]);
							
						} else if (value[0].contentEquals("x")) {
							x = Integer.parseInt(value[1]);
							
						} else if (value[0].contentEquals("y")) {
							y = Integer.parseInt(value[1]);
							
						} else if (value[0].contentEquals("width")) {
							width = Integer.parseInt(value[1]);
							
						} else if (value[0].contentEquals("height")) {
							height = Integer.parseInt(value[1]);
							
						} else if (value[0].contentEquals("xoffset")) {
							xOffset = Integer.parseInt(value[1]);
							
						} else if (value[0].contentEquals("yoffset")) {
							yOffset = Integer.parseInt(value[1]);
							
						} else if (value[0].contentEquals("xadvance")) {
							xAdvance = Integer.parseInt(value[1]);
						}
					}
					
					Glyph g = new Glyph(ascii,
							x * invScaleW,
							y * invScaleH,
							width * invScaleW,
							height * invScaleH,
							xOffset * invScaleW,
							yOffset * invScaleH,
							xAdvance * invScaleW);
					
					alphabet.put(ascii, g);
				}
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("Error finding font file for font " + fontName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading font file for font " + fontName);
			e.printStackTrace();
		}
		
		texture = new Texture("font/" + fontName + ".png", 1, 1);
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public float getLineHeight() {
		return lineHeight;
	}
	
	public Glyph getGlyph(char c) {
		return alphabet.get((int)c);
	}
	
	public void free() {
		texture.free();
	}
	
	public float getSize() {
		return size;
	}
}
