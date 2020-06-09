package chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Vector3i;

import data.BlockData;
import data.Constants;
import store.Database;

public class Chunk {

	public static final int STACK_HEIGHT = 8;
	public static final int MAX_HEIGHT = STACK_HEIGHT * SubChunk.SIZE;
	
	private int absX, absZ;
	
	private SubChunk[] subchunks;
	
	private Map<Vector3i, Integer> modified;
	
	public Chunk(int absX, int absZ, Database db) {
		this.absX = absX;
		this.absZ = absZ;
		
		subchunks = new SubChunk[STACK_HEIGHT];
		
		modified = new HashMap<Vector3i, Integer>();
		
		for (int yIndex = 0; yIndex < STACK_HEIGHT; yIndex++) {
			subchunks[yIndex] = new SubChunk(absX, yIndex * SubChunk.SIZE, absZ);
		}
		
		BlockData stone = Constants.getByName("stone");
		BlockData dirt = Constants.getByName("dirt");
		BlockData grass = Constants.getByName("grass");
		BlockData air = Constants.getByName("air");
		
		for (int x = 0; x < SubChunk.SIZE; x++) {
			for (int z = 0; z < SubChunk.SIZE; z++) {
				setBlockUnmod(x, 0, z, new Block(stone));
				
				for (int y = 1; y < 4; y++) {
					setBlockUnmod(x, y, z, new Block(dirt));
				}
				
				setBlockUnmod(x, 4, z, new Block(grass));
				
				for (int y = 5; y < MAX_HEIGHT; y++) {
					setBlockUnmod(x, y, z, new Block(air));
				}
			}
		}
		
		for (Entry<Vector3i, Integer> entry : db.getChunk(absX, absZ).entrySet()) {
			Vector3i position = entry.getKey();
			
			setBlockUnmod(position.x, position.y, position.z, new Block(Constants.getById(entry.getValue())));
		}
	}
	
	public void generate(Chunk xNeg, Chunk xPos, Chunk zNeg, Chunk zPos) {
		for (int yIndex = 0; yIndex < STACK_HEIGHT; yIndex++) {
			subchunks[yIndex].generate(
					xNeg == null? null : xNeg.getSubchunk(yIndex),
					xPos == null? null : xPos.getSubchunk(yIndex),
					yIndex == 0? null : getSubchunk(yIndex - 1),
					yIndex == STACK_HEIGHT - 1? null : getSubchunk(yIndex + 1),
					zNeg == null? null : zNeg.getSubchunk(yIndex),
					zPos == null? null : zPos.getSubchunk(yIndex));
		}
	}
	
	public Block getBlock(int x, int y, int z) {
		int yIndex = y / SubChunk.SIZE;
		return subchunks[yIndex].getBlock(x, y - yIndex * SubChunk.SIZE, z);
	}
	
	public void setBlockUnmod(int x, int y, int z, Block block) {
		int yIndex = y / SubChunk.SIZE;
		subchunks[yIndex].setBlockUnmod(x, y - yIndex * SubChunk.SIZE, z, block);
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		int yIndex = y / SubChunk.SIZE;
		subchunks[yIndex].setBlock(x, y - yIndex * SubChunk.SIZE, z, block);
		modified.put(new Vector3i(x, y, z), block.getStaticData().id);
	}
	
	public SubChunk getSubchunk(int yIndex) {
		return subchunks[yIndex];
	}
	
	public Map<Vector3i, Integer> getModifiedBlocks() {
		return modified;
	}
	
	public int getAbsX() {
		return absX;
	}
	
	public int getAbsZ() {
		return absZ;
	}
	
	public void free(Database db) {
		db.saveChunk(this);
		
		for (SubChunk subchunk : subchunks)
			subchunk.free();
	}
}
