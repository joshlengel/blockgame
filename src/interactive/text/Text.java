package interactive.text;

import org.joml.Vector3f;

public class Text {

	private TextMesh mesh;
	
	private Vector3f color;
	
	public Text(float x, float y, Font font, float lineLength) {
		mesh = new TextMesh(x, y, font, lineLength);
		
		color = new Vector3f(0.0f, 0.0f, 0.0f);
	}
	
	public void setText(String text) {
		mesh.setText(text);
	}
	
	public void setColor(Vector3f color) {
		this.color = color;
	}
	
	public TextMesh getMesh() {
		return mesh;
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public void free() {
		mesh.free();
	}
}
