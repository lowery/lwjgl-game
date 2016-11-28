package jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.GLBuffers;
import math.VectorMath;
import mesh.Mesh;
import mesh.OBJLoader;
import mesh.Vertex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.List;


public class OneTriangle {
    static int count;

    protected static void setup(GL gl, int width, int height ) {
        GL4 gl4 = gl.getGL4();

        // Source code for vertex and fragment shaders
        String[] vertexShaderSource = loadShaderSource("simple.vert");
        String[] fragmentShaderSource = loadShaderSource("simple.frag");

        // Create and compile vertex shader
        int vertexShader = gl4.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl4.glShaderSource(vertexShader, 1, vertexShaderSource, null, 0);
        gl4.glCompileShader(vertexShader);

        // Create and compile fragment shader
        int fragmentShader = gl4.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl4.glShaderSource(fragmentShader, 1, fragmentShaderSource, null, 0);
        gl4.glCompileShader(fragmentShader);

        // Create program, attach shaders to it, and link it
        int program = gl4.glCreateProgram();
        gl4.glAttachShader(program, vertexShader);
        gl4.glAttachShader(program, fragmentShader);
        gl4.glLinkProgram(program);

        // Delete the shaders as the program has them now
        gl4.glDeleteShader(vertexShader);
        gl4.glDeleteShader(fragmentShader);

        gl4.glUseProgram(program);

        /*
         * Load the vertex data
         */

        OBJLoader loader = new OBJLoader();
        Mesh mesh = loader.load("sphere.obj");
        List<Vertex> vertices = mesh.getVertices();

        float[] vertexData = new float[4 * vertices.size()];
        count = vertices.size();

        for (int i = 0; i < vertices.size(); i++) {
            float[] position = vertices.get(i).getPosition();

            vertexData[(i*4)] = position[0]/mesh.getScale()*0.25f;
            vertexData[(i*4)+1] = position[1]/mesh.getScale()*0.25f;
            vertexData[(i*4)+2] = position[2]/mesh.getScale()*0.25f;
            vertexData[(i*4)+3] = 1.0f;
        }

        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertexData);

        // VAO
        int[] vertexArrayObject = new int[1];
        gl4.glCreateVertexArrays(1, vertexArrayObject, 0);
        gl4.glBindVertexArray(vertexArrayObject[0]);

        // VBO
        int[] vertexBufferObject = new int[1];
        gl4.glGenBuffers(1, vertexBufferObject, 0);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufferObject[0]);
        gl4.glBufferData(GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW);

        gl4.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0); // positions
        gl4.glEnableVertexArrayAttrib(vertexArrayObject[0], 0);
    }

    protected static void render( GL gl, int width, int height ) {
        GL3 gl3 = gl.getGL3();
        GL4 gl4 = gl.getGL4();
        final float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
        gl3.glClearBufferfv(GL2.GL_COLOR, 0, color, 0);

        gl4.glDrawArrays(GL4.GL_TRIANGLES, 0, count);
    }

    private static String[] loadShaderSource(String filename) {
        // Load shader source from resources
        InputStream in = OneTriangle.class.getResourceAsStream("/shaders/" + filename);
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

        return new String[]{source.toString()};
    }
}