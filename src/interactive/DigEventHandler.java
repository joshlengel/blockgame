package interactive;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import chunk.Block;
import chunk.Chunk;
import chunk.ChunkScape;
import chunk.SubChunk;
import data.Constants;
import interactive.inventory.Inventory;
import window.Display;

public class DigEventHandler {

	private Vector3i selectedPosition;
	
	public DigEventHandler() {
		
	}
	
	public void pollEvents(Display display, Camera camera, ChunkScape scape, Inventory inventory) {
		selectedPosition = null;
		
		if (display.getKeyboard().keyPressed(GLFW.GLFW_KEY_LEFT)) {
			inventory.decreaseSlot();
		} else if (display.getKeyboard().keyPressed(GLFW.GLFW_KEY_RIGHT)) {
			inventory.increaseSlot();
		}
		
		for (int key = GLFW.GLFW_KEY_1; key <= GLFW.GLFW_KEY_8; key++) {
			if (display.getKeyboard().keyPressed(key)) {
				inventory.setSlot(key - GLFW.GLFW_KEY_1);
				break;
			}
		}
		
		Ray ray = new Ray(camera);
		
		do {
			Vector3i position = ray.getPosition();
			
			int absIndexX = position.x < 0? (position.x + 1) / SubChunk.SIZE - 1 : position.x / SubChunk.SIZE;
			int absIndexY = position.y < 0? (position.y + 1) / SubChunk.SIZE - 1 : position.y / SubChunk.SIZE;
			int absIndexZ = position.z < 0? (position.z + 1) / SubChunk.SIZE - 1 : position.z / SubChunk.SIZE;
			
			if (absIndexX > scape.getCenterX() + scape.getRenderDistance()
			|| absIndexX < scape.getCenterX() - scape.getRenderDistance()
			|| absIndexZ > scape.getCenterZ() + scape.getRenderDistance()
			|| absIndexZ < scape.getCenterZ() - scape.getRenderDistance()
			|| absIndexY >= Chunk.STACK_HEIGHT
			|| absIndexY < 0) {
				continue;
			}
			
			Chunk chunk = scape.getChunkAbsolute(absIndexX, absIndexZ);
			SubChunk subchunk = chunk.getSubchunk(absIndexY);
			
			int relX = position.x - absIndexX * SubChunk.SIZE;
			int relY = position.y - absIndexY * SubChunk.SIZE;
			int relZ = position.z - absIndexZ * SubChunk.SIZE;
			
			Block block = subchunk.getBlock(relX, relY, relZ);
			
			if (!block.getStaticData().name.contentEquals("air")) {
				selectedPosition = position;
				
				if (display.getMouse().mouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1)) {
					inventory.addItem(block.getStaticData().name);
					
					chunk.setBlock(relX, position.y, relZ, new Block(Constants.getByName("air")));
					
					scape.regenerateSubchunkAbsolute(absIndexX, absIndexY, absIndexZ);
					
					if (relX == 0) {
						scape.regenerateSubchunkAbsolute(absIndexX - 1, absIndexY, absIndexZ);
						
					} else if (relX == SubChunk.SIZE - 1) {
						scape.regenerateSubchunkAbsolute(absIndexX + 1, absIndexY, absIndexZ);
					}
					
					if (relY == 0) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY - 1, absIndexZ);
						
					} else if (relY == SubChunk.SIZE - 1) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY + 1, absIndexZ);
					}
					
					if (relZ == 0) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY, absIndexZ - 1);
						
					} else if (relZ == SubChunk.SIZE - 1) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY, absIndexZ + 1);
					}
					
				} else if (display.getMouse().mouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2)) {
					if (!inventory.available()) break;
					
					Vector3i prev = ray.getPreviousPosition();
					
					absIndexX = prev.x < 0? (prev.x + 1) / SubChunk.SIZE - 1 : prev.x / SubChunk.SIZE;
					absIndexY = prev.y < 0? (prev.y + 1) / SubChunk.SIZE - 1 : prev.y / SubChunk.SIZE;
					absIndexZ = prev.z < 0? (prev.z + 1) / SubChunk.SIZE - 1 : prev.z / SubChunk.SIZE;
					
					if (absIndexX > scape.getCenterX() + scape.getRenderDistance()
					|| absIndexX < scape.getCenterX() - scape.getRenderDistance()
					|| absIndexZ > scape.getCenterZ() + scape.getRenderDistance()
					|| absIndexZ < scape.getCenterZ() - scape.getRenderDistance()
					|| absIndexY >= Chunk.STACK_HEIGHT
					|| absIndexY < 0) {
						break;
					}
					
					chunk = scape.getChunkAbsolute(absIndexX, absIndexZ);
					
					relX = prev.x - absIndexX * SubChunk.SIZE;
					relY = prev.y - absIndexY * SubChunk.SIZE;
					relZ = prev.z - absIndexZ * SubChunk.SIZE;
					
					chunk.setBlock(relX, prev.y, relZ, new Block(Constants.getByName(inventory.removeItem())));
					
					scape.regenerateSubchunkAbsolute(absIndexX, absIndexY, absIndexZ);
					
					if (relX == 0) {
						scape.regenerateSubchunkAbsolute(absIndexX - 1, absIndexY, absIndexZ);
						
					} else if (relX == SubChunk.SIZE - 1) {
						scape.regenerateSubchunkAbsolute(absIndexX + 1, absIndexY, absIndexZ);
					}
					
					if (relY == 0) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY - 1, absIndexZ);
						
					} else if (relY == SubChunk.SIZE - 1) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY + 1, absIndexZ);
					}
					
					if (relZ == 0) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY, absIndexZ - 1);
						
					} else if (relZ == SubChunk.SIZE - 1) {
						scape.regenerateSubchunkAbsolute(absIndexX, absIndexY, absIndexZ + 1);
					}
				}
				break;
			}
		} while (ray.step());
	}
	
	public Vector3i getSelectedPosition() {
		return selectedPosition;
	}
}

class Ray {
	
	private static final float REACH = 10.0f;
	
	private int x, y, z;
	private int prevX, prevY, prevZ;
	
	private int stepX, stepY, stepZ;
	
	private float tMaxX, tMaxY, tMaxZ;
	private float tDeltaX, tDeltaY, tDeltaZ;
	
	public Ray(Camera camera) {
		x = floor(camera.getPosition().x);
		y = floor(camera.getPosition().y);
		z = floor(camera.getPosition().z);
		
		prevX = x;
		prevY = y;
		prevZ = z;
		
		double cosX = Math.cos(camera.getRotation().x);
		Vector3f direction = new Vector3f(
				(float)(Math.sin(camera.getRotation().y) * cosX),
				-(float)(Math.sin(camera.getRotation().x)),
				-(float)(Math.cos(camera.getRotation().y) * cosX));
		
		stepX = direction.x > 0? 1 : (direction.x < 0? -1 : 0);
		stepY = direction.y > 0? 1 : (direction.y < 0? -1 : 0);
		stepZ = direction.z > 0? 1 : (direction.z < 0? -1 : 0);
		
		tMaxX = intBound(camera.getPosition().x, direction.x);
		tMaxY = intBound(camera.getPosition().y, direction.y);
		tMaxZ = intBound(camera.getPosition().z, direction.z);
		
		tDeltaX = stepX / direction.x;
		tDeltaY = stepY / direction.y;
		tDeltaZ = stepZ / direction.z;
	}
	
	public boolean step() {
		if (tMaxX < tMaxY) {
			if (tMaxX < tMaxZ) {
				if (tMaxX > REACH)
					return false;
				
				prevX = x;
				prevY = y;
				prevZ = z;
				
				x += stepX;
				tMaxX += tDeltaX;
			} else {
				if (tMaxZ > REACH)
					return false;
				
				prevX = x;
				prevY = y;
				prevZ = z;
				
				z += stepZ;
				tMaxZ += tDeltaZ;
			}
		} else {
			if (tMaxY < tMaxZ) {
				if (tMaxY > REACH)
					return false;
				
				prevX = x;
				prevY = y;
				prevZ = z;
				
				y += stepY;
				tMaxY += tDeltaY;
			} else {
				if (tMaxZ > REACH)
					return false;
				
				prevX = x;
				prevY = y;
				prevZ = z;
				
				z += stepZ;
				tMaxZ += tDeltaZ;
			}
		}
		
		return true;
	}
	
	public Vector3i getPosition() {
		return new Vector3i(x, y, z);
	}
	
	public Vector3i getPreviousPosition() {
		return new Vector3i(prevX, prevY, prevZ);
	}
	
	private static int floor(float f) {
		return f < 0? (int)f - 1: (int)f;
	}
	
	private static float intBound(float s, float ds) {
		if (s < 0) {
	        return ds < 0? (-s%1 - 1) / ds : -s%1 / ds;
	    } else {
	        return ds < 0? -s%1 / ds : (1 - s%1) / ds;
	    }
	}
}