package rendering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class Shader {

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	Map<String, Integer> uniformLocations;
	
	public Shader(String vertexShaderName, String fragmentShaderName) {
		programID = GL20.glCreateProgram();
		
		vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		GL20.glShaderSource(vertexShaderID, readSource(vertexShaderName));
		GL20.glShaderSource(fragmentShaderID, readSource(fragmentShaderName));
		
		GL20.glCompileShader(vertexShaderID);
		GL20.glCompileShader(fragmentShaderID);
		
		if (GL20.glGetShaderi(vertexShaderID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
			System.err.println("Error compiling vertex shader at /shader/" + vertexShaderName);
			System.err.println(GL20.glGetShaderInfoLog(vertexShaderID));
		}
		
		if (GL20.glGetShaderi(fragmentShaderID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
			System.err.println("Error compiling fragment shader at /shader/" + fragmentShaderName);
			System.err.println(GL20.glGetShaderInfoLog(fragmentShaderID));
		}
		
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
			System.err.println("Error linking shader program");
			System.err.println(GL20.glGetProgramInfoLog(programID));
		}
		
		if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL20.GL_FALSE) {
			System.err.println("Error validating shader program");
			System.err.println(GL20.glGetProgramInfoLog(programID));
		}
	}
	
	public void setUniforms(String... uniforms) {
		uniformLocations = new HashMap<String, Integer>();
		
		for (String uniform : uniforms) {
			uniformLocations.put(uniform, GL20.glGetUniformLocation(programID, uniform));
		}
	}
	
	public void loadUniform(String uniform, Matrix4f value) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		value.get(buffer);
		
		GL20.glUniformMatrix4fv(uniformLocations.get(uniform), false, buffer);
	}
	
	public void loadUniform(String uniform, Vector4f value) {
		GL20.glUniform4f(uniformLocations.get(uniform), value.x, value.y, value.z, value.w);
	}
	
	public void loadUniform(String uniform, Vector3f value) {
		GL20.glUniform3f(uniformLocations.get(uniform), value.x, value.y, value.z);
	}
	
	public void loadUniform(String uniform, Vector3i value) {
		GL20.glUniform3i(uniformLocations.get(uniform), value.x, value.y, value.z);
	}
	
	public void loadUniform(String uniform, Vector2f value) {
		GL20.glUniform2f(uniformLocations.get(uniform), value.x, value.y);
	}
	
	public void loadUniform(String uniform, float value) {
		GL20.glUniform1f(uniformLocations.get(uniform), value);
	}
	
	public void loadUniform(String uniform, int value) {
		GL20.glUniform1i(uniformLocations.get(uniform), value);
	}
	
	public void loadUniform(String uniform, boolean value) {
		GL20.glUniform1i(uniformLocations.get(uniform), value? 1 : 0);
	}
	
	public void activate() {
		GL20.glUseProgram(programID);
	}
	
	public void deactivate() {
		GL20.glUseProgram(0);
	}
	
	public void free() {
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		
		GL20.glDeleteProgram(programID);
	}
	
	private static String readSource(String name) {
		String output = "";
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Shader.class.getResourceAsStream("/shader/" + name)))) {
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				output += line + '\n';
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("Error finding shader source at /shader/" + name);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading shader source at /shader/" + name);
			e.printStackTrace();
		}
		
		return output;
	}
}
