import math.VectorMath;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL45.glCreateBuffers;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

/**
 * Created by lowery on 10/23/2016.
 */
public class Example extends Application {

    private final String VERTEX_SHADER = "triangles.vert";
    private final String FRAGMENT_SHADER = "triangles.frag";

    private final String TITLE = "OpenGL SuperBible Example";

    private final int WIDTH = 500;
    private final int HEIGHT = 500;

    public Example() {
        setWindowProperties(WIDTH, HEIGHT, TITLE);
        setShaders(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    void update(long deltaTime) {

    }

    @Override
    public void render(long deltaTime) {
        // Simply clear the window with red
        final float[] color = {1.0f, 0.0f, 0.0f, 1.0f};
        glClearBufferfv(GL_COLOR, 0, color);

        // Use the program object we created earlier for rendering
        glUseProgram(renderingProgram);

        float[] vertices = {
                0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f
        };

        int[] vertexBufferObject = new int[2];
        glCreateBuffers(vertexBufferObject);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        ;

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexArrayAttrib(vertexArrayObject[0], 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        float[] transform = VectorMath.identity();

        // translate
        glUniformMatrix4fv(1, false, transform);

        // Draw triangles
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    public static void main(String[] args) {
        new Example().run();
    }
}
