import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Application {

	// The window handle
	private long window;

    int[] vertexArrayObject = new int[1];
    int renderingProgram;

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

		int WIDTH = 640;
		int HEIGHT = 480;

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL SuperBible Example", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
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

	private void startup() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        renderingProgram = compileShaders();

        glCreateVertexArrays(vertexArrayObject);
        glBindVertexArray(vertexArrayObject[0]);
    }

	private void loop() {
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {

			render(System.currentTimeMillis());
			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	private void render(long currentTime) {
		// Simply clear the window with red
		final float[] color =  { 1.0f, 0.0f, 0.0f, 1.0f };
		glClearBufferfv(GL_COLOR, 0, color);

        // Use the program object we created earlier for rendering
        glUseProgram(renderingProgram);

        glPointSize(40.0f);

        // Draw one point
        glDrawArrays(GL_TRIANGLES, 0, 3);
	}

	private void shutdown() {
        glDeleteVertexArrays(vertexArrayObject);
        glDeleteProgram(renderingProgram);
    }

	private int compileShaders() {
		// Source code for vertex and fragment shaders
		String vertexShaderSource = loadShaderSource("/shaders/screen.vert");
		String fragmentShaderSource = loadShaderSource("/shaders/screen.frag");

		// Create and compile vertex shader
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);

		// Create and compile fragment shader
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);

		// Create program, attach shaders to it, and link it
		int program = glCreateProgram();
		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);
		glLinkProgram(program);

		// Delete the shaders as the program has them now
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);

		return program;
	}

	private String loadShaderSource(String filename) {
		// Load shader source from resources
		InputStream in = Application.class.getResourceAsStream(filename);
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

		return source.toString();
	}

	public static void main(String[] args) {
		new Application().run();
	}

}