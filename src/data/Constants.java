package data;

import java.io.File;

public class Constants {

	public static final Texture BLOCK_TEXTURE;
	public static final Texture CROSSHAIRS_TEXTURE;
	public static final Texture INVENTORY_BAR_TEXTURE;
	public static final Texture INVENTORY_BAR_SELECTOR_TEXTURE;
	
	public static final CubeMap SKYBOX_TEXTURE;
	
	public static final BlockData[] BLOCKS;
	
	public static final float LIGHT_UP = 1.0f;
	public static final float LIGHT_RL = 0.7f;
	public static final float LIGHT_FB = 0.6f;
	public static final float LIGHT_DOWN = 0.4f;
	
	static {
		BLOCK_TEXTURE = new Texture("block_textures.png", 8, 8);
		CROSSHAIRS_TEXTURE = new Texture("crosshairs.png", 1, 1);
		INVENTORY_BAR_TEXTURE = new Texture("inventory_bar.png", 1, 1);
		INVENTORY_BAR_SELECTOR_TEXTURE = new Texture("inventory_bar_selector.png", 1, 1);
		
		SKYBOX_TEXTURE = new CubeMap(new String[] {"sb_day_right.png", "sb_day_left.png", "sb_day_up.png", "sb_day_down.png", "sb_day_back.png", "sb_day_front.png"});
		
		File[] blockDataFiles = new File("blocks").listFiles((f, name) -> name.substring(name.length() - 3, name.length()).contentEquals(".bd"));
		
		BLOCKS = new BlockData[blockDataFiles.length];
		
		for (int i = 0; i < BLOCKS.length; i++) {
			BLOCKS[i] = new BlockData(blockDataFiles[i].getName());
		}
	}
	
	public static BlockData getByName(String name) {
		for (BlockData data : BLOCKS) {
			if (data.name.contentEquals(name))
				return data;
		}
		
		return null;
	}
	
	public static BlockData getById(int id) {
		for (BlockData data : BLOCKS) {
			if (data.id == id)
				return data;
		}
		
		return null;
	}
	
	public static void free() {
		BLOCK_TEXTURE.free();
		CROSSHAIRS_TEXTURE.free();
		INVENTORY_BAR_TEXTURE.free();
		INVENTORY_BAR_SELECTOR_TEXTURE.free();
		
		SKYBOX_TEXTURE.free();
	}
}
