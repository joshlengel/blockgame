package window;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

public class Mouse {

	private long handle;
	
	private boolean[] mouseButtons;
	
	private double mouseX, mouseY;
	
	protected Mouse(long handle) {
		this.handle = handle;
		
		mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
		
		mouseX = getMouseX();
		mouseY = getMouseY();
		
		lock();
	}
	
	protected void update() {
		for (int mouseButton = 0; mouseButton < GLFW.GLFW_MOUSE_BUTTON_LAST; mouseButton++)
			mouseButtons[mouseButton] = mouseButtonDown(mouseButton);
		
		mouseX = getMouseX();
		mouseY = getMouseY();
	}
	
	public boolean mouseButtonDown(int mouseButton) {
		return GLFW.glfwGetMouseButton(handle, mouseButton) == GLFW.GLFW_PRESS;
	}
	
	public boolean mouseButtonPressed(int mouseButton) {
		return mouseButtonDown(mouseButton) && !mouseButtons[mouseButton];
	}
	
	public boolean mouseButtonReleased(int mouseButton) {
		return !mouseButtonDown(mouseButton) && mouseButtons[mouseButton];
	}
	
	public double getMouseX() {
		DoubleBuffer mouseBuffer = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(handle, mouseBuffer, null);
		
		return mouseBuffer.get();
	}
	
	public double getMouseY() {
		DoubleBuffer mouseBuffer = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(handle, null, mouseBuffer);
		
		return mouseBuffer.get();
	}
	
	public double getMouseDX() {
		return getMouseX() - mouseX;
	}
	
	public double getMouseDY() {
		return getMouseY() - mouseY;
	}
	
	public void lock() {
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}
	
	public void unlock() {
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public void toggle() {
		if (GLFW.glfwGetInputMode(handle, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_NORMAL) {
			lock();
		} else {
			unlock();
		}
	}
	
	public boolean locked() {
		return GLFW.glfwGetInputMode(handle, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
	}
}
