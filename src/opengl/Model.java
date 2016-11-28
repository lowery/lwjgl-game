package opengl;

import mesh.Mesh;
import mesh.OBJLoader;
import mesh.Texture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

/**
 * Created by lowery on 11/21/2016.
 */
public class Model {
    private Mesh mesh;
    private Texture texture;

    int[] vertexArrayObject = new int[1];

    public Model(String meshFile, String textureFile) {
        loadMesh(meshFile);
        loadTexture(textureFile);
    }

    private void loadMesh(String filename) {
        OBJLoader loader = new OBJLoader();
        mesh = loader.load(filename);
        float[] vertexData = mesh.getVertexData();

        // VAO
        vertexArrayObject = new int[1];
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

    public static void loadTexture(String filename) {
        Texture texture = new Texture();
        texture.loadImage(filename);

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

    public void deleteVertexArrays() {
        glDeleteVertexArrays(vertexArrayObject);
    }
}
