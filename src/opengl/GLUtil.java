package opengl;

import mesh.Mesh;
import mesh.OBJLoader;
import mesh.Texture;
import mesh.Vertex;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

/**
 * Created by lowery on 11/21/2016.
 */
public class GLUtil {
    private static final String SHADER_DIR = "/shaders/";

    public static void loadMesh() {
        OBJLoader loader = new OBJLoader();
        Mesh mesh = loader.load("wooden_crate.obj");
        float[] vertexData = mesh.getVertexData();

        // VAO
        int[] vertexArrayObject = new int[1];
        glCreateVertexArrays(vertexArrayObject);
        glBindVertexArray(vertexArrayObject[0]);

        // VBO
        int[] vertexBufferObject = new int[1];
        glGenBuffers(vertexBufferObject);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 6 * Float.BYTES, 0); // positions
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 6 * Float.BYTES, 4 * Float.BYTES); // texture

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    public static void loadTexture() {
        Texture texture = new Texture();
        texture.loadImage("wooden_crate.png");

        int imageWidth = texture.getWidth();
        int imageHeight = texture.getHeight();
        ByteBuffer pixels = texture.getByteBuffer();

        int[] textureObject = new int[1];
        glGenTextures(textureObject);
        glBindTexture(GL_TEXTURE_2D, textureObject[0]);

        // Set our texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // Set texture filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, imageWidth, imageHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);
    }

    public static int compileShaders(String vertexShaderFile, String fragmentShaderFile) {
        // Source code for vertex and fragment shaders
        String vertexShaderSource = loadShaderSource(SHADER_DIR + vertexShaderFile);
        String fragmentShaderSource = loadShaderSource(SHADER_DIR + fragmentShaderFile);

        // Create and compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        // Create and compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);

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

    private static String loadShaderSource(String filename) {
        // Load shader source from resources
        InputStream in = GLUtil.class.getResourceAsStream(filename);
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
}