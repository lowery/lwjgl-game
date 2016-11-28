import math.VectorMath;
import mesh.Mesh;
import mesh.OBJLoader;
import opengl.Shader;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;
import static org.lwjgl.system.MemoryUtil.*;

public class BoundingBoxSphere {
    private final String VERTEX_SHADER = "simple.vert";
    private final String FRAGMENT_SHADER = "simple.frag";

    // The window handle
    private long window;

    private Shader shader;
    private Mesh mesh;
    private int[] vertexArrayObject;

    private float rotAngle;                  // y-axis rotation angle in radians
    private float zPosition = -3.0f;                  // scale
    private int zDirection = 1;                  // positive or negative direction
    private int[] vertexBufferObject;

    private float mouseX = 0.0f;
    private float mouseY = 0.0f;

    private Matrix4f viewProj;
    private Matrix4f invViewProj;
    private float mouseZ = 0.0f;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try {
            init();
            loop();

            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void startup() {
        // Compile the shaders into a program object
        shader = new Shader(VERTEX_SHADER, FRAGMENT_SHADER);

        // Use the program object we created previously for rendering
        shader.useProgram();

        // Enable back face culling
        //glEnable(GL_CULL_FACE);

        // Load mesh from obj file

        //OBJLoader loader = new OBJLoader();
        //mesh = loader.load("sphere.obj");

        /*float[] vertexData = {
                -0.5f, 0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.0f, 1.0f
        };*/


        viewProj = new Matrix4f();
        invViewProj = new Matrix4f();
        viewProj.perspective((float) Math.toRadians(45.0f), 1.0f, 0.0f, 100.0f)
                .lookAt(0.0f, 0.0f, 10f,
                        0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f)
                .invert(invViewProj);
    }

    public void update(long deltaTime) {
        rotAngle += 0.025f;
        zPosition += 0.05f * zDirection;

        if (zPosition > -3.0f) {
            zDirection = -1;
        } else if (zPosition < -10.0f) {
            zDirection = 1;
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        int WIDTH = 1024;
        int HEIGHT = 1024;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                double xPos[] = new double[1];
                double yPos[] = new double[1];

                glfwGetCursorPos(window, xPos, yPos);

                // Normalize
                float x = (float) (((2.0f * xPos[0]) / WIDTH) - 1.0f);
                float y = (float) (1.0f - ((2.0f * yPos[0]) / HEIGHT));
                float z = 1.0f;

                // ray clip
               /* Vector4f ray = new Vector4f(x, y, -1.0f, 1.0f);

                // ray eye
                ray = new Matrix4f().perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f).invert().transform(ray);
                ray = new Vector4f(ray.x(), ray.y(), -1.0f, 0.0f);

                // ray world

               ray = new Matrix4f().lookAt(0.0f, 0.0f, 10f,
                        0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f).invert().transform(ray);

                ray = ray.normalize();

                ray = new Matrix4f().translate(0.0f, 0.0f, -10.f).transform(ray);
               System.out.println(ray.x() + " : " + ray.y() + " : " + ray.z());

                */

               Vector4f ray = new Vector4f();
                Vector4f viewport = new Vector4f(0.0f, 0.0f, 1024.0f, 1024.0f);

                //new Matrix4f().unproject(xPos[0], yPos[0], 0.0f, new int[]{0, 0, 1024, 1024}, ray)
                //invViewProj.transform(ray);

                mouseX = ray.x();
                mouseY = ray.y();
                mouseZ = ray.z();
            }
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        long lastTime, deltaTime;
        lastTime = System.currentTimeMillis();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        startup();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            deltaTime = System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            // view matrix
            /*float[] transform = VectorMath.multiply(
                    VectorMath.rotate(yRotAngle / 2, yRotAngle, 0.0f),
                    VectorMath.scale(scale*0.25f, scale*0.25f, scale*0.25f)
            );*/

            //Matrix4f model = new Matrix4f().rotate(rotAngle/2, 1.0f, 0.0f, 0.0f).rotate(rotAngle, 0.0f, 1.0f, 0.0f);

           /* Matrix4f invModel = new Matrix4f();
            Matrix4f model = new Matrix4f();
            model.identity()
                    .invert(invModel);

            Matrix4f invView = new Matrix4f();
            Matrix4f view = new Matrix4f();
            view.lookAt(0.0f, 1.0f, 10.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f)
                    .invert(invView);

            Matrix4f invProj = new Matrix4f();
            Matrix4f proj = new Matrix4f();
            proj.perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f)
                    .invert(invProj);

            Matrix4f mvp = proj.mul(view).mul(model);*/


            float[] vertexData = {
                    -1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, -1.0f, 0.0f, 1.0f,
                    -1.0f, -1.0f, 0.0f, 1.0f,

                    mouseX, mouseY, mouseZ, 1.0f
            };

            // VAO
            vertexArrayObject = new int[1];
            glCreateVertexArrays(vertexArrayObject);
            glBindVertexArray(vertexArrayObject[0]);

            // VBO
            vertexBufferObject = new int[1];
            glGenBuffers(vertexBufferObject);

            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
            glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);

            glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0); // positions
            glEnableVertexAttribArray(0);

            FloatBuffer fb = BufferUtils.createFloatBuffer(16);
            //glUniformMatrix4fv(1, false, new Matrix4f().identity().get(fb));

            glUniformMatrix4fv(1, false, viewProj.get(fb));

            // Setup first draw call
            glPointSize(10f);

            glUniform4fv(2, new float[]{1.0f, 0.0f, 0.0f, 1.0f});
            glDrawArrays(GL_LINE_LOOP, 0, 4);
            glDrawArrays(GL_POINTS, 4, 1);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            update(deltaTime);
        }
    }

    public static void main(String[] args) {
        new BoundingBoxSphere().run();
    }

}