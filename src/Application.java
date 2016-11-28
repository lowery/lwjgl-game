import input.KeyEvent;
import input.MouseEvent;
import mesh.Mesh;
import mesh.OBJLoader;
import mesh.Texture;
import opengl.Model;
import opengl.Shader;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL44.glBufferStorage;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

public abstract class Application {

	// The window handle
	private long window;

	private int width;
	private int height;
	private String title;

    private Shader shader;
    private Model model;

	private String vertexShaderFile;
	private String fragmentShaderFile;

	private String meshFile;
	private String textureFile;

	protected Queue<KeyEvent> keyEvents = new LinkedList<>();
	protected Queue<MouseEvent> mouseEvents = new LinkedList<>();

	protected void setWindowProperties(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	protected void setShaders(String vertexShaderFile, String fragmentShaderFile) {
		this.vertexShaderFile = vertexShaderFile;
		this.fragmentShaderFile = fragmentShaderFile;
	}

	protected void setTexture(String textureFile) {
		this.textureFile = textureFile;
	}

	protected void setMesh(String meshFile) {
		this.meshFile = meshFile;
	}

	public void run() {
		try {
			init();
            startup();
			loop();
            shutdown();

			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		} finally {
			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
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

		// Create the window
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
			}

			if (action == GLFW_PRESS || action == GLFW_RELEASE) {
				switch (key) {
					case GLFW_KEY_SPACE:
					case GLFW_KEY_W:
					case GLFW_KEY_A:
					case GLFW_KEY_S:
					case GLFW_KEY_D:
					case GLFW_KEY_E:
					case GLFW_KEY_Q:
						keyEvents.add(new KeyEvent(key, action));
						break;
				}
			}
		});

		glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
			mouseEvents.add(new MouseEvent(MouseEvent.EVT_MOUSE_SCROLL, xoffset, yoffset));
		});

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(
			window,
			(vidmode.width() - width) / 2,
			(vidmode.height() - height) / 2
		);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void startup() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // On window resize
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

		// Compile the shaders into a program object
		shader = new Shader(vertexShaderFile, fragmentShaderFile);

		// Use the program object we created previously for rendering
        shader.useProgram();

		// Enable back face culling
		glEnable(GL_CULL_FACE);

        // Load assets into buffers

		model = new Model(meshFile, textureFile);
    }

	private void loop() {
        long lastTime, deltaTime;

        lastTime = System.currentTimeMillis();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
            deltaTime = System.currentTimeMillis() - lastTime;
            lastTime = System.currentTimeMillis();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            update(deltaTime);

			//render(deltaTime);
            render(deltaTime);
			glfwSwapBuffers(window); // swap the color buffers

		}
	}

    abstract void update(long deltaTime);
    abstract void render(long deltaTime);

	private void shutdown() {
        model.deleteVertexArrays();
        shader.deleteProgram();
    }
}