import math.VectorMath;
import mesh.Mesh;
import mesh.OBJLoader;
import mesh.Vertex;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public class SpinningCube extends Application {

    private final String VERTEX_SHADER = "screen.vert";
    private final String FRAGMENT_SHADER = "screen.frag";

    private float yRotAngle;                  // y-axis rotation angle in radians
    private float scale = .5f;                  // scale
    private int scaleSign = 1;                  // positive or negative scale

    public SpinningCube(int width, int height, String title) {
        setWindowProperties(width, height, title);
        setShaders(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void update(long deltaTime) {
        yRotAngle += 0.05f;
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

        // Use the program object we created earlier for rendering
        glUseProgram(renderingProgram);
        glEnable(GL_CULL_FACE);

        final float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
        glClearBufferfv(GL_COLOR, 0, color);

        /*float[] vertexPositions = {
                // front face
                -0.25f, -0.25f, 0.25f, 1.0f,
                0.25f, -0.25f, 0.25f, 1.0f,
                -0.25f, 0.25f, 0.25f, 1.0f,

                -0.25f, 0.25f, 0.25f, 1.0f,
                0.25f, -0.25f, 0.25f, 1.0f,
                0.25f, 0.25f, 0.25f, 1.0f,

                // right face
                0.25f, -0.25f, 0.25f, 1.0f,
                0.25f, -0.25f, -0.25f, 1.0f,
                0.25f, 0.25f, 0.25f, 1.0f,

                0.25f, 0.25f, 0.25f, 1.0f,
                0.25f, -0.25f, -0.25f, 1.0f,
                0.25f, 0.25f, -0.25f, 1.0f,

                // back face

                0.25f, -0.25f, -0.25f, 1.0f,
                -0.25f, -0.25f, -0.25f, 1.0f,
                -0.25f, 0.25f, -0.25f, 1.0f,

                -0.25f, 0.25f, -0.25f, 1.0f,
                0.25f, 0.25f, -0.25f, 1.0f,
                0.25f, -0.25f, -0.25f, 1.0f,

                // left face
                -0.25f, -0.25f, -0.25f, 1.0f,
                -0.25f, -0.25f, 0.25f, 1.0f,
                -0.25f, 0.25f, 0.25f, 1.0f,

                -0.25f, 0.25f, 0.25f, 1.0f,
                -0.25f, 0.25f, -0.25f, 1.0f,
                -0.25f, -0.25f, -0.25f, 1.0f,

                // bottom face
                0.25f, -0.25f, 0.25f, 1.0f,
                -0.25f, -0.25f, 0.25f, 1.0f,
                -0.25f, -0.25f, -0.25f, 1.0f,

                -0.25f, -0.25f, -0.25f, 1.0f,
                0.25f, -0.25f, -0.25f, 1.0f,
                0.25f, -0.25f, 0.25f, 1.0f,

                // top face
                -0.25f, 0.25f, 0.25f, 1.0f,
                0.25f, 0.25f, 0.25f, 1.0f,
                -0.25f, 0.25f, -0.25f, 1.0f,

                -0.25f, 0.25f, -0.25f, 1.0f,
                0.25f, 0.25f, 0.25f, 1.0f,
                0.25f, 0.25f, -0.25f, 1.0f
        };*/


        OBJLoader loader = new OBJLoader();
        Mesh mesh = loader.load("wooden_crate.obj");
        List<Vertex> vertexData = mesh.getVertices();

        float[] vertexPositions = new float[vertexData.size() * 4];

        for (int i = 0; i < vertexData.size(); i++) {
            Vertex v = vertexData.get(i);

            float[] position = v.getPosition();
            float[] texCoords = v.getTexCoords();

            vertexPositions[i*4] = position[0]*0.1f;
            vertexPositions[(i*4)+1] = position[1]*0.1f;
            vertexPositions[(i*4)+2] = position[2]*0.1f;
            vertexPositions[(i*4)+3] = 1.0f;
        }

        // VAO
        int[] vertexArrayObject = new int[1];
        glCreateVertexArrays(vertexArrayObject);
        glBindVertexArray(vertexArrayObject[0]);

        // VBO
        int[] vertexBufferObject = new int[1];
        glGenBuffers(vertexBufferObject);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
        glBufferData(GL_ARRAY_BUFFER, vertexPositions, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexArrayAttrib(vertexArrayObject[0], 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        float[] transform = VectorMath.multiply(
                VectorMath.rotate(yRotAngle / 2, yRotAngle, 0.0f),
                VectorMath.scale(scale, scale, scale)
        );

        //float[] transform = VectorMath.identity();

        // rotate and scale
        glUniformMatrix4fv(1, false, transform);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public static void main(String[] args) {
        new SpinningCube(500, 500, "OpenGL Spinning Cube").run();
    }

}