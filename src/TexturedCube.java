import math.VectorMath;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL45.*;

public class TexturedCube extends Application {

    private final String VERTEX_SHADER = "texture.vert";
    private final String FRAGMENT_SHADER = "texture.frag";

    private float yRotAngle;                  // y-axis rotation angle in radians
    private float scale = .5f;                  // scale
    private int scaleSign = 1;                  // positive or negative scale

    public TexturedCube(int width, int height, String title) {
        setWindowProperties(width, height, title);
        setShaders(VERTEX_SHADER, FRAGMENT_SHADER);
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

        /*float[] vertexPositions = {
                // front face
                -1f, -1f, 0.0f, 1.0f,
                1f, -1f, 0.0f, 1.0f,
                -1f, 1f, 0.0f, 1.0f,

                -1f, 1f, 0.0f, 1.0f,
                1f, -1f, 0.0f, 1.0f,
                1f, 1f, 0.0f, 1.0f,
        };*/


        /*float[] vertices = {
                // Positions                // Colors                 // Texture Coords
                0.5f,  0.5f, 0.0f, 1.0f,    1.0f, 0.0f, 0.0f, 1.0f,   1.0f, 1.0f,               // Top Right
                0.5f, -0.5f, 0.0f, 1.0f,    0.0f, 1.0f, 0.0f, 1.0f,   1.0f, 0.0f,               // Bottom Right
                -0.5f, -0.5f, 0.0f, 1.0f,   0.0f, 0.0f, 1.0f, 1.0f,   0.0f, 0.0f,               // Bottom Left
        };*/

        float[] vertices = {
                // front face
                -0.25f, -0.25f, 0.25f, 1.0f,    0.0f, 0.0f,
                0.25f, -0.25f, 0.25f, 1.0f,     1.0f, 0.0f,
                -0.25f, 0.25f, 0.25f, 1.0f,     0.0f, 1.0f,

                -0.25f, 0.25f, 0.25f, 1.0f,     0.0f, 1.0f,
                0.25f, -0.25f, 0.25f, 1.0f,     1.0f, 0.0f,
                0.25f, 0.25f, 0.25f, 1.0f,      1.0f, 1.0f,

                // right face
                0.25f, -0.25f, 0.25f, 1.0f,     0.0f, 0.0f,
                0.25f, -0.25f, -0.25f, 1.0f,    1.0f, 0.0f,
                0.25f, 0.25f, 0.25f, 1.0f,      0.0f, 1.0f,

                0.25f, 0.25f, 0.25f, 1.0f,      0.0f, 1.0f,
                0.25f, -0.25f, -0.25f, 1.0f,    1.0f, 0.0f,
                0.25f, 0.25f, -0.25f, 1.0f,     1.0f, 1.0f,

                // back face

                0.25f, -0.25f, -0.25f, 1.0f,    0.0f, 0.0f,
                -0.25f, -0.25f, -0.25f, 1.0f,   1.0f, 0.0f,
                -0.25f, 0.25f, -0.25f, 1.0f,    1.0f, 1.0f,

                -0.25f, 0.25f, -0.25f, 1.0f,    1.0f, 1.0f,
                0.25f, 0.25f, -0.25f, 1.0f,     0.0f, 1.0f,
                0.25f, -0.25f, -0.25f, 1.0f,    0.0f, 0.0f,

                // left face
                -0.25f, -0.25f, -0.25f, 1.0f,   0.0f, 0.0f,
                -0.25f, -0.25f, 0.25f, 1.0f,    1.0f, 0.0f,
                -0.25f, 0.25f, 0.25f, 1.0f,     1.0f, 1.0f,

                -0.25f, 0.25f, 0.25f, 1.0f,     1.0f, 1.0f,
                -0.25f, 0.25f, -0.25f, 1.0f,    0.0f, 1.0f,
                -0.25f, -0.25f, -0.25f, 1.0f,   0.0f, 0.0f,

                // bottom face
                0.25f, -0.25f, 0.25f, 1.0f,     1.0f, 1.0f,
                -0.25f, -0.25f, 0.25f, 1.0f,    0.0f, 1.0f,
                -0.25f, -0.25f, -0.25f, 1.0f,   0.0f, 0.0f,

                -0.25f, -0.25f, -0.25f, 1.0f,   0.0f, 0.0f,
                0.25f, -0.25f, -0.25f, 1.0f,    1.0f, 0.0f,
                0.25f, -0.25f, 0.25f, 1.0f,     1.0f, 1.0f,

                // top face
                -0.25f, 0.25f, 0.25f, 1.0f,     0.0f, 0.0f,
                0.25f, 0.25f, 0.25f, 1.0f,      1.0f, 0.0f,
                -0.25f, 0.25f, -0.25f, 1.0f,    0.0f, 1.0f,

                -0.25f, 0.25f, -0.25f, 1.0f,    0.0f, 1.0f,
                0.25f, 0.25f, 0.25f, 1.0f,      1.0f, 0.0f,
                0.25f, 0.25f, -0.25f, 1.0f,      1.0f, 1.0f
        };

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
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 6 * Float.BYTES, 4 * Float.BYTES); // texture

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        //glEnableVertexAttribArray(2);

        //glBindBuffer(GL_ARRAY_BUFFER, 0);

        // texture
        ByteBuffer pixels = loadTexture("crate.png");

        int[] textureObject = new int[1];
        glGenTextures(textureObject);
        glBindTexture(GL_TEXTURE_2D, textureObject[0]);

        // Set our texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // Set texture filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 256, 256, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);

        float[] transform = VectorMath.rotate(yRotAngle / 2, yRotAngle, 0.0f);

        glUniformMatrix4fv(2, false, transform);

        glDrawArrays(GL_TRIANGLES, 0, 36);
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
        new TexturedCube(1024, 1024, "OpenGL Textured Cube").run();
    }

}