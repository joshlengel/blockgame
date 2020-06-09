package interactive;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import window.Display;

public class Camera {

	private Vector3f position;
	private Vector3f velocity;
	private Vector2f rotation;
	
	private Matrix4f viewMatrix;
	
	private float speed;
	private float acceleration;
	private float drag;
	
	private float sensitivity;
	
	public Camera(Vector3f position, Vector2f rotation) {
		this.position = position;
		this.velocity = new Vector3f();
		this.rotation = rotation;
		
		viewMatrix = new Matrix4f();
		
		updateViewMatrix();
		
		speed = 15.0f;
		acceleration = 5.0f;
		drag = 0.95f;
		
		sensitivity = 0.001f;
	}
	
	public void update(Display display, double dt) {
		double speedFactor = speed * dt;
		double accelerationFactor = acceleration * dt;
		
		if (display.getKeyboard().keyDown(GLFW.GLFW_KEY_W)) {
			velocity.x += Math.sin(rotation.y) * accelerationFactor;
			velocity.z -= Math.cos(rotation.y) * accelerationFactor;
		}
		
		if (display.getKeyboard().keyDown(GLFW.GLFW_KEY_S)) {
			velocity.x -= Math.sin(rotation.y) * accelerationFactor;
			velocity.z += Math.cos(rotation.y) * accelerationFactor;
		}
		
		if (display.getKeyboard().keyDown(GLFW.GLFW_KEY_A)) {
			velocity.x -= Math.cos(rotation.y) * accelerationFactor;
			velocity.z -= Math.sin(rotation.y) * accelerationFactor;
		}
		
		if (display.getKeyboard().keyDown(GLFW.GLFW_KEY_D)) {
			velocity.x += Math.cos(rotation.y) * accelerationFactor;
			velocity.z += Math.sin(rotation.y) * accelerationFactor;
		}
		
		if (display.getKeyboard().keyDown(GLFW.GLFW_KEY_SPACE)) {
			velocity.y += accelerationFactor;
		}
		
		if (display.getKeyboard().keyDown(GLFW.GLFW_KEY_LEFT_SHIFT) | display.getKeyboard().keyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
			velocity.y -= accelerationFactor;
		}
		
		rotation.x += display.getMouse().getMouseDY() * sensitivity;
		rotation.y += display.getMouse().getMouseDX() * sensitivity;
		
		velocity.x = velocity.x > speed? speed : (velocity.x < -speed? -speed : velocity.x);
		velocity.y = velocity.y > speed? speed : (velocity.y < -speed? -speed : velocity.y);
		velocity.z = velocity.z > speed? speed : (velocity.z < -speed? -speed : velocity.z);
		
		position.x += velocity.x * speedFactor;
		position.y += velocity.y * speedFactor;
		position.z += velocity.z * speedFactor;
		
		velocity.mul(drag);
		
		updateViewMatrix();
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector2f getRotation() {
		return rotation;
	}
	
	private void updateViewMatrix() {
		viewMatrix.identity();
		viewMatrix.rotateX(rotation.x);
		viewMatrix.rotateY(rotation.y);
		viewMatrix.translate(-position.x, -position.y, -position.z);
	}
}
