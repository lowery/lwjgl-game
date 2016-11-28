import math.VectorMath;
import mesh.Mesh;
import mesh.OBJLoader;
import mesh.Vertex;
import opengl.Model;
import opengl.Shader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
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
        setMesh("wooden_crate.obj");
        setTexture("wooden_crate.png");
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
        final float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
        glClearBufferfv(GL_COLOR, 0, color);

        float[] transform = VectorMath.multiply(
                VectorMath.rotate(yRotAngle / 2, yRotAngle, 0.0f),
                VectorMath.scale(scale*0.25f, scale*0.25f, scale*0.25f)
        );

        glUniformMatrix4fv(2, false, transform);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public static void main(String[] args) {
        new TexturedCube(1024, 1024, "OpenGL Textured Cube").run();
    }

}