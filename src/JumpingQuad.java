import input.KeyEvent;
import math.VectorMath;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public class JumpingQuad extends Application {

    private final String VERTEX_SHADER = "triangles.vert";
    private final String FRAGMENT_SHADER = "triangles.frag";

    private State state = State.IDLE;

    private float positionY;                  // Position of the character
    private float velocityY;                  // Velocity of the character
    private float gravity = 0.0001f;          // How strong is gravity

    public JumpingQuad(int width, int height, String title) {
        setWindowProperties(width, height, title);
        setShaders(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void update(long deltaTime) {
        while (!keyEvents.isEmpty()) {
            KeyEvent evt = keyEvents.poll();

            if (evt.key == GLFW_KEY_SPACE && evt.action == GLFW_PRESS) {

                state = State.JUMPING;
                velocityY = 0.01f;
            }
        }

        if (state == State.JUMPING) {
            positionY += velocityY * deltaTime;
            velocityY -= gravity * deltaTime;

            if (positionY < 0 || positionY == 0) {
                positionY = 0;
                velocityY = 0;

                state = State.IDLE;
            }
        }
    }

    @Override
    public void render(long deltaTime) {
        // Compile the shaders into a program object
        int renderingProgram = compileShaders(VERTEX_SHADER, FRAGMENT_SHADER);

        // Simply clear the window with black
        final float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
        glClearBufferfv(GL_COLOR, 0, color);

        // Use the program object we created earlier for rendering
        glUseProgram(renderingProgram);

        float[] vertices = {
                // Left bottom triangle
                -0.125f, 0.125f, 0f, 1.0f,
                -0.125f, -0.125f, 0f, 1.0f,
                0.125f, -0.125f, 0f, 1.0f,
                // Right top triangle
                0.125f, -0.125f, 0f, 1.0f,
                0.125f, 0.125f, 0f, 1.0f,
                -0.125f, 0.125f, 0f, 1.0f
        };

        // VAO
        int[] vertexArrayObject = new int[1];
        glCreateVertexArrays(vertexArrayObject);
        glBindVertexArray(vertexArrayObject[0]);

        // VBO
        int[] vertexBufferObject = new int[2];
        glCreateBuffers(vertexBufferObject);

        // Quad vertices
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        ;

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexArrayAttrib(vertexArrayObject[0], 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        float[] transform = VectorMath.multiply(VectorMath.scale(0.5f, 0.5f, 0.5f),
                VectorMath.translate(0.0f, positionY, 0.0f));

        // translate
        glUniformMatrix4fv(1, false, transform);

        // Draw triangles
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public static void main(String[] args) {
        new JumpingQuad(500, 500, "OpenGL Jumping Square").run();
    }

    private enum State {
        IDLE, JUMPING
    }
}