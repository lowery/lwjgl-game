import mesh.Mesh;
import mesh.OBJLoader;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {
	private final String VERTEX_SHADER = "simple.vert";
	private final String FRAGMENT_SHADER = "simple.frag";

	private Mesh mesh;
	private int[] vertexArrayObject;
	private int[] vertexBufferObject;

	Vector3f rayOrigin;
	Vector3f rayDir;

	// The window handle
	private long window;
	private float[] vertices;

	Vector4f aabMin = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
	Vector4f aabMax = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

	private Matrix4f proj;

	// camera
	private Vector3f eye;
	private Vector3f center;
	private Vector3f up;

	private Matrix4f model;
	private float rotAngle;                  // y-axis rotation angle in radians
	private float zPosition = -3.0f;                  // position
	private int zDirection = 1;                  // positive or negative direction

	boolean intersect = false;
	private double mouseX;
	private double mouseY;

	private long handCursor = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
	private long arrowCursor = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

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

		glfwSetCursorPosCallback(window, (window, xPos, yPos) -> {
			mouseX = xPos;
			mouseY = yPos;
		});

		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
				double xPos[] = new double[1];
				double yPos[] = new double[1];

				glfwGetCursorPos(window, xPos, yPos);

				intersect = checkIntersection(xPos[0], yPos[0]);
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

		startup();

		long lastTime, deltaTime;
		lastTime = System.currentTimeMillis();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			deltaTime = System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();

			render();
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			update(deltaTime);
		}
	}

	private void startup() {
		// Enable back face culling
		//glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glPolygonOffset(1, 1);
		glEnable(GL_POLYGON_OFFSET_FILL);

		// Enable blending
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Load mesh from obj file
		OBJLoader loader = new OBJLoader();
		mesh = loader.load("sphere.obj");

		// VAO
		vertexArrayObject = new int[1];
		glCreateVertexArrays(vertexArrayObject);
		glBindVertexArray(vertexArrayObject[0]);

		// create 3 VBOs
		vertexBufferObject = new int[3];
		glGenBuffers(vertexBufferObject);

		// bind first vbo
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
		glBufferData(GL_ARRAY_BUFFER, mesh.getVertexData(), GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0); // unbind

		// bind second vbo
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[1]);
		glBufferData(GL_ARRAY_BUFFER, mesh.getBoundingBox().getVertexData(), GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0); // unbind

		// bind third vbo
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[2]);
		glBufferData(GL_ARRAY_BUFFER, mesh.getBoundingBox().getPolyVertexData(), GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0); // unbind

		// Enable vertex array attributes
		glEnableVertexAttribArray(0);
	}

	public void update(long deltaTime) {
		rotAngle += 0.025f;
		zPosition += 0.1f * zDirection;

		if (zPosition > -2.0f) {
			zDirection = -1;
		} else if (zPosition < -10.0f) {
			zDirection = 1;
		}
	}

	private boolean checkIntersection(double xPos, double yPos) {
		// note: must reverse y
		Matrix4f view = new Matrix4f();
		view.lookAt(eye, center, up);

		Matrix4f mvp = new Matrix4f();
		proj.mul(view, mvp);
		mvp.mul(model);

		//System.out.println(aabMax.x() + " : " + aabMax.y() + " : " + aabMax.z());

		rayOrigin = new Vector3f();
		rayDir = new Vector3f();

		// box
		Vector3f min = new Vector3f(aabMin.x(), aabMin.y(), aabMin.z());
		Vector3f max = new Vector3f(aabMax.x(), aabMax.y(), aabMax.z());

		Vector2f result = new Vector2f();
		mvp.unprojectRay((float) xPos, (float) yPos, new int[]{0, 0, 1024, 1024}, rayOrigin, rayDir);
		return Intersectionf.intersectRayAab(rayOrigin, rayDir, min, max, result);
	}

	private void render() {

		// Compile the shaders into a program object
		int renderingProgram = compileShaders(VERTEX_SHADER, FRAGMENT_SHADER);

		// Use the program object we created earlier for rendering
		glUseProgram(renderingProgram);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// projection matrix
		proj = new Matrix4f();
		proj.perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f);

		// camera
		eye = new Vector3f(0.0f, 0.0f, 5.0f);
		center = new Vector3f(0.0f, 0.0f, 0.0f);
		up = new Vector3f(0.0f, 1.0f, 0.0f);

		Matrix4f view = new Matrix4f();
		view.lookAt(eye, center, up);

		model = new Matrix4f();
		model.translate(0.0f, 0.0f, zPosition).rotate(rotAngle/2, 0.0f, 1.0f, 0.0f).rotate(rotAngle, 1.0f, 0.0f, 0.0f);

		Matrix4f mvp = new Matrix4f();
		proj.mul(view, mvp);
		mvp.mul(model);

		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		glUniformMatrix4fv(1, false, mvp.get(fb));

		// Setup mesh draw call
		glUniform4fv(2, new float[]{0.50f, .50f, 0.50f, 1.0f});

		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 6 * Float.BYTES, 0); // positions

		glDrawArrays(GL_TRIANGLES, 0, mesh.getVertices().size());

		if (intersect) {
			// Setup bounding box lines draw call
			glUniform4fv(2, new float[]{0.0f, 1.0f, 0.0f, 1.0f});

			glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[1]);
			glVertexAttribPointer(0, 4, GL_FLOAT, false, 6 * Float.BYTES, 0); // positions


			glDrawArrays(GL_LINE_LOOP, 0, 4); // front face
			glDrawArrays(GL_LINE_LOOP, 4, 4); // back face
			glDrawArrays(GL_LINES, 8, 8); // connect sides


			// Setup bounding box polys draw call
			glUniform4fv(2, new float[]{0.0f, 1.0f, 0.0f, 0.3f});

			glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[2]);
			glVertexAttribPointer(0, 4, GL_FLOAT, false, 6 * Float.BYTES, 0); // positions

			glDrawArrays(GL_TRIANGLES, 0, 36);
		}
	}


	public int compileShaders(String vertexShaderFile, String fragmentShaderFile) {
		// Source code for vertex and fragment shaders
		String vertexShaderSource = loadShaderSource("/shaders/" + vertexShaderFile);
		String fragmentShaderSource = loadShaderSource("/shaders/" + fragmentShaderFile);

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
		new HelloWorld().run();
	}

	float[] vertexData = {
			// front face
			-1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, -1.0f, 1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f, 1.0f,

			// back face
			-1.0f, 1.0f, -1.0f, 1.0f,
			1.0f, 1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, -1.0f, 1.0f,

			// left side
			-1.0f, 1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f, 1.0f,

			// right side
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, 1.0f, 1.0f
	};

}