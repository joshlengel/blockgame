package store;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class DataFile {

	private File worldFolder;
	private File loadFile;
	
	public static class LoadInfo {
		public Vector3f camPos;
		public Vector2f camRot;
	}
	
	public DataFile(String name) {
		worldFolder = new File("save/" + name);
		loadFile = new File(worldFolder, "load.txt");
		
		if (!worldFolder.exists()) {
			if (!worldFolder.mkdir())
				System.err.println("Error creating save folder for world \"" + name + "\"");
			
			generateLoadFile();
		}
	}
	
	private void generateLoadFile() {
		try {
			if (!loadFile.createNewFile())
				System.err.println("Error creating load data file for world \"" + worldFolder.getName() + "\"");
		} catch (IOException e) {
			System.err.println("Error creating load file");
			e.printStackTrace();
		}
		
		LoadInfo info = new LoadInfo();
		info.camPos = new Vector3f(0, 5, 0);
		info.camRot = new Vector2f(0, 0);
		
		saveLoadInfo(info);
	}
	
	public LoadInfo getLoadInfo() {
		File loadFile = new File(worldFolder, "load.txt");
		
		LoadInfo info = new LoadInfo();
		info.camPos = new Vector3f();
		info.camRot = new Vector2f();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(loadFile))) {
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("-cam ")) {
					String[] parameters = line.split(" +");
					
					info.camPos.x = Float.parseFloat(parameters[1]);
					info.camPos.y = Float.parseFloat(parameters[2]);
					info.camPos.z = Float.parseFloat(parameters[3]);
					
					info.camRot.x = Float.parseFloat(parameters[4]);
					info.camRot.y = Float.parseFloat(parameters[5]);
				}
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("Error finding load file for world \"" + worldFolder.getName() + "\"");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading load data for world \"" + worldFolder.getName() + "\"");
			e.printStackTrace();
		}
		
		return info;
	}
	
	public void saveLoadInfo(LoadInfo info) {
		File loadFile = new File(worldFolder, "load.txt");
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(loadFile))) {
			
			writer.write("-cam "
					+ info.camPos.x + " "
					+ info.camPos.y + " "
					+ info.camPos.z + " "
					+ info.camRot.x + " "
					+ info.camRot.y + "\n");
			
		} catch (IOException e) {
			System.err.println("Error saving load data for world \"" + worldFolder.getName() + "\"");
			e.printStackTrace();
		}
	}
}
