package chunk;

public class SubChunk {

	public static final int SIZE = 16;
	public static final int SIZE_SQUARED = SIZE * SIZE;
	public static final int SIZE_CUBED = SIZE_SQUARED * SIZE;
	
	private int absX, absY, absZ;
	private Mesh mesh;
	
	private Block[] blocks;
	
	public SubChunk(int absX, int absY, int absZ) {
		this.absX = absX;
		this.absY = absY;
		this.absZ = absZ;
		
		mesh = new Mesh();
		
		blocks = new Block[SIZE_CUBED];
	}
	
	public void generate(SubChunk xNeg, SubChunk xPos, SubChunk yNeg, SubChunk yPos, SubChunk zNeg, SubChunk zPos) {
		mesh.clear();
		
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					
					Block current = getBlock(x, y, z);
					
					if (!current.getStaticData().regular)
						continue;
					
					Block blockXNeg = x == 0? (xNeg == null? null : xNeg.getBlock(SIZE - 1, y, z)) : getBlock(x - 1, y, z);
					Block blockYNeg = y == 0? (yNeg == null? null : yNeg.getBlock(x, SIZE - 1, z)) : getBlock(x, y - 1, z);
					Block blockZNeg = z == 0? (zNeg == null? null : zNeg.getBlock(x, y, SIZE - 1)) : getBlock(x, y, z - 1);
					Block blockXPos = x == SIZE - 1? (xPos == null? null : xPos.getBlock(0, y, z)) : getBlock(x + 1, y, z);
					Block blockYPos = y == SIZE - 1? (yPos == null? null : yPos.getBlock(y, 0, z)) : getBlock(x, y + 1, z);
					Block blockZPos = z == SIZE - 1? (zPos == null? null : zPos.getBlock(x, y, 0)) : getBlock(x, y, z + 1);
					
					if (blockXNeg == null || blockXNeg.getStaticData().transparent) {
						mesh.addBlockFace(x + absX, y + absY, z + absZ, Face.LEFT, current.getStaticData());
					}
					
					if (blockYNeg == null || blockYNeg.getStaticData().transparent) {
						mesh.addBlockFace(x + absX, y + absY, z + absZ, Face.DOWN, current.getStaticData());
					}
					
					if (blockZNeg == null || blockZNeg.getStaticData().transparent) {
						mesh.addBlockFace(x + absX, y + absY, z + absZ, Face.FRONT, current.getStaticData());
					}
					
					if (blockXPos == null || blockXPos.getStaticData().transparent) {
						mesh.addBlockFace(x + absX, y + absY, z + absZ, Face.RIGHT, current.getStaticData());
					}
					
					if (blockYPos == null || blockYPos.getStaticData().transparent) {
						mesh.addBlockFace(x + absX, y + absY, z + absZ, Face.UP, current.getStaticData());
					}
					
					if (blockZPos == null || blockZPos.getStaticData().transparent) {
						mesh.addBlockFace(x + absX, y + absY, z + absZ, Face.BACK, current.getStaticData());
					}
				}
			}
		}
		
		mesh.generate();
	}
	
	public Block getBlock(int x, int y, int z) {
		return blocks[z * SIZE_SQUARED + y * SIZE + x];
	}
	
	public void setBlockUnmod(int x, int y, int z, Block block) {
		blocks[z * SIZE_SQUARED + y * SIZE + x] = block;
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		setBlockUnmod(x, y, z, block);
		/// TODO save modified blocks
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public int getAbsX() {
		return absX;
	}
	
	public int getAbsY() {
		return absY;
	}
	
	public int getAbsZ() {
		return absZ;
	}
	
	public void free() {
		mesh.free();
	}
}
