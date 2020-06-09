package chunk;

import store.Database;

public class ChunkScape {

	private int centerX, centerZ;
	
	private Chunk[] chunks;
	
	private int renderDistance;
	private int doubleRenderDistance;
	
	public ChunkScape(int renderDistance, Database db) {
		this.renderDistance = renderDistance;
		
		doubleRenderDistance = 2 * renderDistance + 1;
		
		centerX = 0;
		centerZ = 0;
		
		chunks = new Chunk[doubleRenderDistance * doubleRenderDistance];
		
		for (int xIndex = 0; xIndex < doubleRenderDistance; xIndex++) {
			for (int zIndex = 0; zIndex < doubleRenderDistance; zIndex++) {
				int chunkX = (xIndex - renderDistance + centerX) * SubChunk.SIZE;
				int chunkZ = (zIndex - renderDistance + centerZ) * SubChunk.SIZE;
				setChunkRelative(xIndex, zIndex, new Chunk(chunkX, chunkZ, db));
			}
		}
		
		for (int xIndex = 0; xIndex < doubleRenderDistance; xIndex++) {
			for (int zIndex = 0; zIndex < doubleRenderDistance; zIndex++) {
				getChunkRelative(xIndex, zIndex).generate(
						xIndex == 0? null : getChunkRelative(xIndex - 1, zIndex),
						xIndex == doubleRenderDistance - 1? null : getChunkRelative(xIndex + 1, zIndex),
						zIndex == 0? null : getChunkRelative(xIndex, zIndex - 1),
						zIndex == doubleRenderDistance - 1? null : getChunkRelative(xIndex, zIndex + 1));
			}
		}
	}
	
	public void regenerateSubchunkAbsolute(int xIndex, int yIndex, int zIndex) {
		
		getChunkAbsolute(xIndex, zIndex).getSubchunk(yIndex).generate(
				xIndex == centerX - renderDistance? null : getChunkAbsolute(xIndex - 1, zIndex).getSubchunk(yIndex),
				xIndex == centerX + renderDistance? null : getChunkAbsolute(xIndex + 1, zIndex).getSubchunk(yIndex),
				yIndex == 0? null : getChunkAbsolute(xIndex, zIndex).getSubchunk(yIndex - 1),
				yIndex == Chunk.STACK_HEIGHT - 1? null : getChunkAbsolute(xIndex, zIndex).getSubchunk(yIndex + 1),
				zIndex == centerZ - renderDistance? null : getChunkAbsolute(xIndex, zIndex - 1).getSubchunk(yIndex),
				zIndex == centerZ + renderDistance? null : getChunkAbsolute(xIndex, zIndex + 1).getSubchunk(yIndex));
	}
	
	private Chunk getChunkRelative(int xIndex, int zIndex) {
		return chunks[zIndex * doubleRenderDistance + xIndex];
	}
	
	private void setChunkRelative(int xIndex, int zIndex, Chunk chunk) {
		chunks[zIndex * doubleRenderDistance + xIndex] = chunk;
	}
	
	public Chunk getChunk(int xIndex, int zIndex) {
		return getChunkRelative(xIndex + renderDistance, zIndex + renderDistance);
	}
	
	public Chunk getChunkAbsolute(int xIndex, int zIndex) {
		return getChunkRelative(xIndex + renderDistance - centerX, zIndex + renderDistance - centerZ);
	}
	
	public int getRenderDistance() {
		return renderDistance;
	}
	
	public int getCenterX() {
		return centerX;
	}
	
	public int getCenterZ() {
		return centerZ;
	}
	
	public void free(Database db) {
		for (Chunk chunk : chunks)
			chunk.free(db);
	}
}
