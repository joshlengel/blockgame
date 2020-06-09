package chunk;

import data.BlockData;

public class Block {

	public static final int TRANSPARENT = 0x01;
	public static final int REGULAR = 0x02;
	
	private BlockData type;
	private byte power;
	//private byte flags;
	
	public Block(BlockData type) {
		this.type = type;
	}
	
	public BlockData getStaticData() {
		return type;
	}
	
	public byte getPower() {
		return power;
	}
}
