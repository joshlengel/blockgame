package store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Vector3i;

import chunk.Chunk;

public class Database {

	private static final String PATH = "save/blocks.db";
	
	private Connection db;
	
	private PreparedStatement addBlock;
	private PreparedStatement getBlocks;
	
	public Database() {
		try {
			Class.forName("org.sqlite.JDBC");
			
			db = DriverManager.getConnection("jdbc:sqlite:" + PATH);
			
		} catch (ClassNotFoundException e) {
			System.err.println("Error loading sqlite database native. Check for existance of jar");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Error opening block database");
			e.printStackTrace();
		}
	}
	
	public void createWorld() {
		try {
			Statement stmt = db.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS world (chunk_x integer, chunk_z integer, x integer, y integer, z integer, id integer, UNIQUE(chunk_x, chunk_z, x, y, z));");
			stmt.close();
		} catch (SQLException e) {
			System.err.println("Error creating world");
			e.printStackTrace();
		}
	}
	
	public void prepareStatements(String worldName) {
		try {
			addBlock = db.prepareStatement("INSERT OR REPLACE INTO " + worldName + " (chunk_x, chunk_z, x, y, z, id) VALUES (@chunk_x, @chunk_z, @x, @y, @z, @id);");
			getBlocks = db.prepareStatement("SELECT x, y, z, id FROM " + worldName + " WHERE chunk_x=@chunk_x AND chunk_z=@chunk_z;");
		} catch (SQLException e) {
			System.err.println("Error preparing statements for world " + worldName);
			e.printStackTrace();
		}
	}
	
	public void saveChunk(Chunk chunk) {
		try {
			addBlock.setInt(1, chunk.getAbsX());
			addBlock.setInt(2, chunk.getAbsZ());
			
			for (Entry<Vector3i, Integer> entry : chunk.getModifiedBlocks().entrySet()) {
				Vector3i position = entry.getKey();
				
				addBlock.setInt(3, position.x);
				addBlock.setInt(4, position.y);
				addBlock.setInt(5, position.z);
				addBlock.setInt(6, entry.getValue());
				addBlock.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Error adding chunk blocks");
			e.printStackTrace();
		}
	}
	
	public Map<Vector3i, Integer> getChunk(int chunkX, int chunkZ) {
		Map<Vector3i, Integer> blocks = new HashMap<Vector3i, Integer>();
		
		try {
			getBlocks.setInt(1, chunkX);
			getBlocks.setInt(2, chunkZ);
			ResultSet rs = getBlocks.executeQuery();
			
			while (rs.next()) {
				Vector3i position = new Vector3i(rs.getInt(1), rs.getInt(2), rs.getInt(3));
				int id = rs.getInt(4);
				
				blocks.put(position, id);
			}
		} catch (SQLException e) {
			System.err.println("Error getting blocks at " + chunkX + " " + chunkZ);
			e.printStackTrace();
		}
		
		return blocks;
	}
	
	public void free() {
		try {
			db.close();
		} catch (SQLException e) {
			System.err.println("Error closing block database");
			e.printStackTrace();
		}
	}
}
