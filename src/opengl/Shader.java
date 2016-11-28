package opengl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

/**
 * Created by lowery on 11/21/2016.
 */
public class Shader {
    int renderingProgram;

    public Shader(String vertexShaderFile, String fragmentShaderFile) {
        renderingProgram = compileShaders(vertexShaderFile, fragmentShaderFile);
    }

    private int compileShaders(String vertexShaderFile, String fragmentShaderFile) {
        // Source code for vertex and fragment shaders
        String vertexShaderSource = loadShaderSource("/shaders/" + vertexShaderFile);
        String fragmentShaderSource = loadShaderSource("/shaders/" + fragmentShaderFile);

        // Create and compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        // Create and compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        // Create program, attach shaders to it, and link it
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        // Delete the shaders as the program has them now
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    private String loadShaderSource(String filename) {
        // Load shader source from resources
        InputStream in = Shader.class.getResourceAsStream(filename);
        StringBuilder source = new StringBuilder();

        // Read input line by line and append to string builder
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;

            while ((line = reader.readLine()) != null) {
                source.append(line).append('\n');
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return source.toString();
    }

    public void useProgram() {
        glUseProgram(renderingProgram);
    }

    public void deleteProgram() {
        glDeleteProgram(renderingProgram);
    }
}
