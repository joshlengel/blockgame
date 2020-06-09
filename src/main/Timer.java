package main;

public class Timer {

	private static final int TO_NANOS = (int)1E9;
	private static final double TO_SECONDS = 1E-9;
	
	private long invFps;
	
	private long time;
	private double delta;
	
	public Timer(int fps) {
		invFps = TO_NANOS / fps;
	}
	
	public boolean updateRequested() {
		if (time == 0l) {
			time = System.nanoTime();
			return false;
		}
		
		long nextTime = System.nanoTime();
		long diff = nextTime - time;
		
		if (diff > invFps) {
			time = nextTime;
			delta = diff * TO_SECONDS;
			return true;
		} else {
			return false;
		}
	}
	
	public void setFPS(int fps) {
		this.invFps = TO_NANOS / fps;
	}
	
	public double getDelta() {
		return delta;
	}
}
