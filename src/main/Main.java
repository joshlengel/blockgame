package main;

import java.text.DecimalFormat;

import org.joml.Vector3f;

import chunk.ChunkScape;
import data.Constants;
import interactive.Camera;
import interactive.DigEventHandler;
import interactive.inventory.Inventory;
import interactive.text.Font;
import interactive.text.Text;
import rendering.ChunkRenderer;
import rendering.CrosshairsRenderer;
import rendering.InventoryRenderer;
import rendering.QuadRenderer;
import rendering.SelectedBlockRenderer;
import rendering.SkyboxRenderer;
import rendering.TextRenderer;
import store.DataFile;
import store.DataFile.LoadInfo;
import store.Database;
import window.Display;

public class Main {

	private static final int DEFAULT_FPS = 60;
	
	public static void main(String[] args) {
		Display display = new Display();
		
		Database db = new Database();
		db.createWorld();
		db.prepareStatements("world");
		
		DataFile dataFile = new DataFile("world");
		LoadInfo info = dataFile.getLoadInfo();
		
		Camera camera = new Camera(info.camPos, info.camRot);
		
		Inventory inventory = new Inventory();
		
		Timer timer = new Timer(DEFAULT_FPS);
		
		ChunkScape scape = new ChunkScape(6, db);
		
		Font arial = new Font("arial", 0.5f);
		
		Text fpsLabel = new Text(-1.0f, 1.0f, arial, 2.0f);
		fpsLabel.setColor(new Vector3f(1.0f, 1.0f, 1.0f));
		
		ChunkRenderer cRenderer = new ChunkRenderer();
		CrosshairsRenderer chRenderer = new CrosshairsRenderer();
		SelectedBlockRenderer sbRenderer = new SelectedBlockRenderer();
		SkyboxRenderer skyboxRenderer = new SkyboxRenderer();
		TextRenderer textRenderer = new TextRenderer();
		InventoryRenderer invRenderer = new InventoryRenderer(inventory);
		
		DigEventHandler handler = new DigEventHandler();
		
		while (!display.closed()) {
			if (timer.updateRequested()) {
				
				DecimalFormat df = new DecimalFormat("#0.00");
				fpsLabel.setText("FPS: " + df.format(1.0 / timer.getDelta()));
				
				display.update();
				
				if (display.getMouse().locked()) {
					camera.update(display, timer.getDelta());
					
					handler.pollEvents(display, camera, scape, inventory);
					invRenderer.update(arial);
				}
				
				cRenderer.render(display, camera, scape);
				sbRenderer.render(display, camera, handler.getSelectedPosition());
				skyboxRenderer.render(display, camera);
				invRenderer.render(display);
				chRenderer.render(display);
				textRenderer.render(display, fpsLabel);
				
				display.swapBuffers();
			}
		}
		
		display.free();
		
		dataFile.saveLoadInfo(info);
		
		scape.free(db);
		
		fpsLabel.free();
		
		cRenderer.free();
		chRenderer.free();
		sbRenderer.free();
		invRenderer.free();
		skyboxRenderer.free();
		textRenderer.free();
		
		db.free();
		
		QuadRenderer.freeStatic();
		Constants.free();
	}
}
