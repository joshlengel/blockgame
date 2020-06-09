package interactive.text;

public class Glyph {

	private int ascii;
	private float x;
	private float y;
	private float width;
	private float height;
	private float xOffset;
	private float yOffset;
	private float xAdvance;
	
	public Glyph(int ascii, float x, float y, float width, float height, float xOffset, float yOffset, float xAdvance) {
		super();
		this.ascii = ascii;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xAdvance = xAdvance;
	}

	public int getAscii() {
		return ascii;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getxOffset() {
		return xOffset;
	}

	public float getyOffset() {
		return yOffset;
	}

	public float getxAdvance() {
		return xAdvance;
	}
}
