import math.VectorMath;
import mesh.Mesh;
import mesh.OBJLoader;
import mesh.Vertex;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL45.*;

public class SphereTest extends Application {

    private final String VERTEX_SHADER = "screen.vert";
    private final String FRAGMENT_SHADER = "screen.frag";

    private float yRotAngle;                  // y-axis rotation angle in radians
    private float scale = .5f;                  // scale
    private int scaleSign = 1;                  // positive or negative scale

    float[] vertices;
    ByteBuffer texture;

    private int triangles;

    public SphereTest(int width, int height, String title) {
        setWindowProperties(width, height, title);
        setShaders(VERTEX_SHADER, FRAGMENT_SHADER);


        OBJLoader loader = new OBJLoader();
        Mesh mesh = loader.load("sphere.obj");
        List<Vertex> vertexData = mesh.getVertices();
        triangles = vertexData.size();

        vertices = new float[vertexData.size() * 6];

        for (int i = 0; i < vertexData.size(); i++) {
            Vertex v = vertexData.get(i);

            float[] position = v.getPosition();
            float[] texCoords = v.getTexCoords();

            vertices[i*6] = position[0]/mesh.getScale();
            vertices[(i*6)+1] = position[1]/mesh.getScale();
            vertices[(i*6)+2] = position[2]/mesh.getScale();
            vertices[(i*6)+3] = 1.0f;

            vertices[(i*6)+4] = texCoords[0];
            vertices[(i*6)+5] = texCoords[1];
        }

        texture = loadTexture("wooden_crate.png");
    }

    @Override
    public void update(long deltaTime) {
        yRotAngle += 0.025f;
        scale += 0.01f * scaleSign;

        if (scale > 2.0f) {
            scaleSign = -1;
        } else if (scale < 0.5f) {
            scaleSign = 1;
        }
    }

    @Override
    public void render(long deltaTime) {
        // Compile the shaders into a program object
        int renderingProgram = compileShaders(VERTEX_SHADER, FRAGMENT_SHADER);

        // Use the program object we created previously for rendering
        glUseProgram(renderingProgram);
        glEnable(GL_CULL_FACE);

        final float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
        glClearBufferfv(GL_COLOR, 0, color);

        // VAO
        int[] vertexArrayObject = new int[1];
        glCreateVertexArrays(vertexArrayObject);
        glBindVertexArray(vertexArrayObject[0]);

        // VBO
        int[] vertexBufferObject = new int[1];
        glGenBuffers(vertexBufferObject);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 6 * Float.BYTES, 0); // positions
        //glVertexAttribPointer(1, 4, GL_FLOAT, false, 10 * Float.BYTES, 4 * Float.BYTES); // colors
        //glVertexAttribPointer(2, 2, GL_FLOAT, false, 6 * Float.BYTES, 4 * Float.BYTES); // texture

        glEnableVertexAttribArray(0);
        //glEnableVertexAttribArray(1);
        //glEnableVertexAttribArray(2);

        //glBindBuffer(GL_ARRAY_BUFFER, 0);

        // texture
        //ByteBuffer pixels = texture;
        //int imageHeight = 1024;
        //int imageWidth = 1024;

       // int[] textureObject = new int[1];
        //glGenTextures(textureObject);
        //glBindTexture(GL_TEXTURE_2D, textureObject[0]);

        // Set our texture parameters
       // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
       // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // Set texture filtering
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, imageWidth, imageHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);

        float[] transform = VectorMath.multiply(
                VectorMath.rotate(yRotAngle / 2, yRotAngle, 0.0f),
                VectorMath.scale(scale*0.25f, scale*0.25f, scale*0.25f)
        );

        glUniformMatrix4fv(1, false, transform);

        glDrawArrays(GL_TRIANGLES, 0, triangles);
    }

    public ByteBuffer loadTexture(String filename) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/textures/" + filename));

            int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

            ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3); //4 for RGBA, 3 for RGB

            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    int i = y * image.getWidth() + x;
                    int pixel = pixels[i];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    buffer.put((byte) (pixel & 0xFF));               // Blue component
                    //buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }

            buffer.flip();

            return buffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new SphereTest(500, 500, "OpenGL Sphere Test").run();
    }

}