package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.joml.Vector2f;

public class BlockData {
	
	public String name;
	public int id;
	public boolean transparent;
	public boolean regular;
	public boolean collidable;
	
	public int[] index;
	public Vector2f[] texCoords;
	
	public BlockData(String blockDataName) {
		try (BufferedReader reader = new BufferedReader(new FileReader("blocks/" + blockDataName))) {
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				if (line.startsWith("name: ")) {
					name = line.substring(6);
					
				} else if (line.startsWith("id: ")) {
					id = Integer.parseInt(line.substring(4));
					
				} else if (line.contentEquals("transparent")) {
					transparent = true;
					
				} else if (line.contentEquals("regular")) {
					regular = true;
					
				} else if (line.contentEquals("collidable")) {
					collidable = true;
					
				} else if (line.startsWith("index: ")) {
					index = new int[3];
					texCoords = new Vector2f[3];
					
					String[] nums = line.substring(7).split(" +");
					
					for (int i = 0; i < 3; i++) {
						index[i] = Integer.parseInt(nums[i]);
						
						texCoords[i] = new Vector2f(
								index[i] % Constants.BLOCK_TEXTURE.getAtlasWidth() * Constants.BLOCK_TEXTURE.getUnitWidth(),
								(int)(index[i] * Constants.BLOCK_TEXTURE.getUnitWidth()) * Constants.BLOCK_TEXTURE.getUnitHeight());
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("Block data file at blocks/" + blockDataName + " not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading block data file at blocks/" + blockDataName);
			e.printStackTrace();
		}
	}
}
