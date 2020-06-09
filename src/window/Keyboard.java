package window;

import org.lwjgl.glfw.GLFW;

public class Keyboard {

	private long handle;
	
	private boolean[] keys;
	
	protected Keyboard(long handle) {
		this.handle = handle;
		
		keys = new boolean[GLFW.GLFW_KEY_LAST];
	}
	
	protected void update() {
		for (int key = 0; key < GLFW.GLFW_KEY_LAST; key++)
			keys[key] = keyDown(key);
	}
	
	public boolean keyDown(int key) {
		return GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
	}
	
	public boolean keyPressed(int key) {
		return keyDown(key) && !keys[key];
	}
	
	public boolean keyReleased(int key) {
		return !keyDown(key) && keys[key];
	}
}
