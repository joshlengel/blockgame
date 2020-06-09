package window;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Display {

	private int width, height;
	private float aspectRatio;
	private long handle;
	
	private Keyboard keyboard;
	private Mouse mouse;
	
	private float fov;
	private float tanHalfFov;
	private float clipNear, clipFar;
	private Matrix4f projectionMatrix;
	
	private float red, green, blue, alpha;
	
	public Display() {
		if (!GLFW.glfwInit()) {
			System.err.println("Error initializing GLFW");
		}
		
		GLFWVidMode screen = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		
		width = (int)(screen.width() * 0.75);
		height = (int)(screen.height() * 0.75);
		aspectRatio = (float) width / height;
		
		fov = 70.0f;
		tanHalfFov = (float) Math.tan(fov * 0.5d);
		clipNear = 0.1f;
		clipFar = 300.0f;
		
		projectionMatrix = new Matrix4f();
		
		updateProjectionMatrix();
		
		setBackgroundColor(0x87CEEBFF);
		
		handle = GLFW.glfwCreateWindow(width, height, "Block Game!", 0l, 0l);
		
		if (handle == 0l) {
			System.err.println("Error creating window");
		}
		
		GLFW.glfwSetCursorPos(handle, 0.0d, 0.0d);
		
		keyboard = new Keyboard(handle);
		mouse = new Mouse(handle);
		
		GLFW.glfwSetWindowPos(handle, (screen.width() - width) / 2, (screen.height() - height) / 2);
		
		GLFW.glfwMakeContextCurrent(handle);
		GL.createCapabilities();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GLFW.glfwShowWindow(handle);
	}
	
	public boolean closed() {
		return GLFW.glfwWindowShouldClose(handle);
	}
	
	public void update() {
		if (keyboard.keyPressed(GLFW.GLFW_KEY_ESCAPE))
			mouse.toggle();
		
		keyboard.update();
		mouse.update();
		
		GL11.glClearColor(red, green, blue, alpha);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		IntBuffer widthBuff = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuff = BufferUtils.createIntBuffer(1);
		
		GLFW.glfwGetWindowSize(handle, widthBuff, heightBuff);
		
		if (widthBuff.get(0) != width || heightBuff.get(0) != height) {
			width = widthBuff.get(0);
			height = heightBuff.get(0);
			
			aspectRatio = (float) width / height;
			
			updateProjectionMatrix();
			
			GL11.glViewport(0, 0, width, height);
		}
		
		GLFW.glfwPollEvents();
	}
	
	public void swapBuffers() {
		GLFW.glfwSwapBuffers(handle);
	}
	
	public Keyboard getKeyboard() {
		return keyboard;
	}
	
	public Mouse getMouse() {
		return mouse;
	}
	
	public float getAspectRatio() {
		return aspectRatio;
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public void setBackgroundColor(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public void setBackgroundColor(int rgba) {
		int alpha = rgba & 0x000000FF;
		int blue = (rgba >>= 8) & 0x000000FF;
		int green = (rgba >>= 8) & 0x000000FF;
		int red = (rgba >>= 8) & 0x000000FF;
		
		this.red = red / 255.0f;
		this.green = green / 255.0f;
		this.blue = blue / 255.0f;
		this.alpha = alpha / 255.0f;
	}
	
	public Vector3f getBackgroundColor() {
		return new Vector3f(red, green, blue);
	}
	
	private void updateProjectionMatrix() {
		projectionMatrix.m00(1.0f / (tanHalfFov * aspectRatio));
		projectionMatrix.m11(1.0f / tanHalfFov);
		projectionMatrix.m22(-(clipFar + clipNear) / (clipFar - clipNear));
		projectionMatrix.m32(-2.0f * clipNear * clipFar / (clipFar - clipNear));
		projectionMatrix.m23(-1.0f);
		projectionMatrix.m33(0.0f);
	}
	
	public void free() {
		GLFW.glfwTerminate();
	}
}