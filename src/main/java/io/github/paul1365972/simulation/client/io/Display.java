package io.github.paul1365972.simulation.client.io;

import io.github.paul1365972.simulation.client.Simulation;
import io.github.paul1365972.simulation.client.main.ApplicationConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;

public class Display {
	
	public static final double ASPECT_RATIO = (double) 16 / 9;
	private static final Logger LOGGER = LogManager.getLogger();
	private static long window;
	
	private static volatile int windowWidth = 800, windowHeight = 450;
	private static int lastWidth = 800, lastHeight = 450;
	private static int viewWindowWidth = -1, viewWindowHeight = -1;
	
	private static volatile int realFramebufferWidth = -1, realFramebufferHeight = -1;
	private static int framebufferWidth = -1, framebufferHeight = -1;
	private static int viewFramebufferWidth = -1, viewFramebufferHeight = -1;
	
	private static boolean resizedFlag = true;
	
	private static boolean fullscreen = false;
	
	private static ConcurrentLinkedQueue<InputEvent> inputEventQueue = new ConcurrentLinkedQueue<>();
	
	private static long primaryMonitor;
	private static GLFWVidMode vidmode;
	
	private Display() {
	}
	
	public static boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public static void close() {
		assertMainThread();
		Callbacks.glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}
	
	public static void swapBuffers() {
		glfwSwapBuffers(window);
	}
	
	public static void updateDimensions() {
		int rfbw = realFramebufferWidth;
		int rfbh = realFramebufferHeight;
		if (rfbw != framebufferWidth || rfbh != framebufferHeight) {
			framebufferWidth = rfbw;
			framebufferHeight = rfbh;
			
			boolean wide = ASPECT_RATIO <= rfbw / (double) rfbh;
			viewFramebufferWidth = wide ? (int) (Math.round(rfbh * ASPECT_RATIO)) : rfbw;
			viewFramebufferHeight = wide ? rfbh : (int) (Math.round(rfbw / ASPECT_RATIO));
			
			resizedFlag = true;
		}
	}
	
	public static void waitEvents() {
		assertMainThread();
		glfwWaitEvents();
	}
	
	public static void terminate() {
		assertMainThread();
		close();
		glfwTerminate();
	}
	
	public static int getViewWindowWidth() {
		return viewWindowWidth;
	}
	
	public static int getViewWindowHeight() {
		return viewWindowHeight;
	}
	
	public static int getViewFramebufferWidth() {
		return viewFramebufferWidth;
	}
	
	public static int getViewFramebufferHeight() {
		return viewFramebufferHeight;
	}
	
	public static int getWindowWidth() {
		return windowWidth;
	}
	
	public static int getWindowHeight() {
		return windowHeight;
	}
	
	public static int getFramebufferWidth() {
		return framebufferWidth;
	}
	
	public static int getFramebufferHeight() {
		return framebufferHeight;
	}
	
	public static boolean isFullscreen() {
		return fullscreen;
	}
	
	public static void setFullscreen(boolean state) {
		assertMainThread();
		if (fullscreen == state)
			return;
		if (state) {
			lastWidth = windowWidth;
			lastHeight = windowHeight;
			glfwSetWindowMonitor(window, primaryMonitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
		} else {
			glfwSetWindowMonitor(window, 0, (vidmode.width() - lastWidth) / 2, (vidmode.height() - lastHeight) / 2, lastWidth, lastHeight, vidmode.refreshRate());
		}
		fullscreen = state;
	}
	
	public static boolean pollResized() {
		boolean tmp = resizedFlag;
		resizedFlag = false;
		return tmp;
	}
	
	public static void suggestClose() {
		glfwSetWindowShouldClose(window, true);
	}
	
	public static void makeContextCurrent(ApplicationConfiguration config) {
		LOGGER.debug("Making context current");
		glfwMakeContextCurrent(window);
		glfwSwapInterval(config.isVsync() ? 1 : 0);
		
		GLCapabilities caps = GL.createCapabilities();
		config.setCapabilities(caps);
	}
	
	public static void createDisplay(String title, ApplicationConfiguration config) {
		assertMainThread();
		LOGGER.debug("Creating Window");
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		vidmode = glfwGetVideoMode(primaryMonitor = glfwGetPrimaryMonitor());
		config.setVidMode(vidmode);
		
		setWindowHints();
		
		window = glfwCreateWindow(windowWidth, windowHeight, title, 0, 0);
		if (window == 0)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetWindowPos(window, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);
		
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		glfwSetCursor(window, glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR));
		
		int fbw, fbh, ww, wh;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buffer1 = stack.mallocInt(1);
			IntBuffer buffer2 = stack.mallocInt(1);
			
			glfwGetFramebufferSize(window, buffer1, buffer2);
			fbw = buffer1.get(0);
			fbh = buffer2.get(0);
			
			glfwGetWindowSize(window, buffer1, buffer2);
			ww = buffer1.get(0);
			wh = buffer2.get(0);
		}
		
		GLFWFramebufferSizeCallbackI framebufferSizeCallback = (long window, int w, int h) -> {
			realFramebufferWidth = Math.max(1, w);
			realFramebufferHeight = Math.max(1, h);
		};
		GLFWWindowSizeCallbackI windowSizeCallback = (long window, int w, int h) -> {
			windowWidth = Math.max(1, w);
			windowHeight = Math.max(1, h);
			
			boolean wide = ASPECT_RATIO <= windowWidth / (double) windowHeight;
			viewWindowWidth = wide ? (int) (Math.round(windowHeight * ASPECT_RATIO)) : windowWidth;
			viewWindowHeight = wide ? windowHeight : (int) (Math.round(windowWidth / ASPECT_RATIO));
		};
		
		GLFWCursorPosCallbackI cursorPosCallback = (long window, double xpos, double ypos) -> {
			double offX = (windowWidth - viewWindowWidth) / 2.0;
			double offY = (windowHeight - viewWindowHeight) / 2.0f;
			inputEventQueue.add(new InputEvent.CursorPos(xpos, ypos, offX, offY, viewWindowWidth, viewWindowHeight));
		};
		
		framebufferSizeCallback.invoke(window, fbw, fbh);
		windowSizeCallback.invoke(window, ww, wh);
		updateDimensions();
		cursorPosCallback.invoke(window, windowWidth / 2, windowHeight / 2);
		
		glfwSetCursorPosCallback(window, cursorPosCallback);
		glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);
		glfwSetWindowSizeCallback(window, windowSizeCallback);
		
		glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods) -> {
			if (key == GLFW_KEY_ESCAPE) {
				if (action == GLFW_RELEASE)
					suggestClose();
			} else if (key == GLFW_KEY_F11) {
				if (action == GLFW_RELEASE)
					setFullscreen(!fullscreen);
			} else if (action != GLFW_REPEAT) {
				inputEventQueue.add(new InputEvent.Key(key, scancode, action == GLFW_PRESS, mods));
			}
		});
		
		glfwSetCharModsCallback(window, (long window, int codepoint, int mods) -> {
			inputEventQueue.add(new InputEvent.Char(new String(Character.toChars(codepoint)), mods));
		});
		
		glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> {
			if (action != GLFW_REPEAT)
				inputEventQueue.add(new InputEvent.MouseButton(button, mods, action == GLFW_PRESS));
		});
		
		glfwSetScrollCallback(window, (long window, double xoffset, double yoffset) -> {
			inputEventQueue.add(new InputEvent.Scroll(xoffset, yoffset));
		});
		
		glfwSetCursorEnterCallback(window, (long window, boolean entered) -> {
		
		});
		glfwSetWindowFocusCallback(window, (long window, boolean focused) -> {
			inputEventQueue.add(new InputEvent.Focus(focused));
		});
		
		glfwShowWindow(window);
		LOGGER.debug("Showing Window");
	}
	
	private static void setWindowHints() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
		glfwWindowHint(GLFW_CENTER_CURSOR, GLFW_TRUE);
		
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		
		if (Simulation.DEBUG)
			glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
	}
	
	private static void assertMainThread() {
		assert Thread.currentThread().getId() == 1;
	}
	
	public static class Inputs {
		
		private static InputEvent.CursorPos lastCursorPos;
		private static boolean[] pressedKeys = new boolean[GLFW_KEY_LAST];
		
		public static int sendEvents(EventListener listener) {
			int processed = 0;
			InputEvent e;
			while ((e = inputEventQueue.poll()) != null) {
				processed++;
				if (e instanceof InputEvent.CursorPos) {
					lastCursorPos = (InputEvent.CursorPos) e;
					listener.onCursor((InputEvent.CursorPos) e);
				} else if (e instanceof InputEvent.MouseButton) {
					listener.onMouse((InputEvent.MouseButton) e, lastCursorPos);
				} else if (e instanceof InputEvent.Scroll) {
					listener.onScroll((InputEvent.Scroll) e, lastCursorPos);
				} else if (e instanceof InputEvent.Key) {
					pressedKeys[((InputEvent.Key) e).getKey()] = ((InputEvent.Key) e).isPressed();
					listener.onKey((InputEvent.Key) e);
				} else if (e instanceof InputEvent.Char) {
					listener.onChar((InputEvent.Char) e);
				} else if (e instanceof InputEvent.Focus) {
					listener.onFocus((InputEvent.Focus) e);
				} else {
					throw new RuntimeException("Strange InputEvent: " + e.getClass().getTypeName() + ", " + e.toString());
				}
			}
			return processed;
		}
		
		public static InputEvent.CursorPos getCursorPos() {
			return lastCursorPos;
		}
		
		public static boolean isPressed(int key) {
			return pressedKeys[key];
		}
	}
	
}
