package io.github.paul1365972.simulation.client;

import io.github.paul1365972.simulation.client.io.Display;
import io.github.paul1365972.simulation.client.main.ApplicationConfiguration;
import io.github.paul1365972.simulation.client.util.TaskScheduler;
import io.github.paul1365972.simulation.client.util.Timer;
import io.github.paul1365972.simulation.client.util.Waiter;
import io.github.paul1365972.simulation.renderer.MasterRenderer;
import io.github.paul1365972.simulation.renderer.loader.Loader;
import io.github.paul1365972.simulation.renderer.utils.GLHelper;
import io.github.paul1365972.simulation.renderer.utils.GLUtils;
import io.github.paul1365972.simulation.util.Math;
import io.github.paul1365972.simulation.world.WorldState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.imageio.ImageIO;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulation implements TaskScheduler {
	
	public static final String NAME = "Collision Simulation";
	public static final String VERSION = "0.0.1 Snapshot";
	public static final boolean DEBUG = Boolean.getBoolean("customdebug");
	private static final Logger LOGGER = LogManager.getLogger();
	private ApplicationConfiguration config;
	
	private volatile boolean running;
	private AtomicInteger fpsCounter = new AtomicInteger(), tpsCounter = new AtomicInteger(), eventsCounter = new AtomicInteger(), stepsCounter = new AtomicInteger();
	
	private Queue<Runnable> scheduledTasks = new ConcurrentLinkedQueue<>();
	
	private Timer timer;
	
	private MasterRenderer renderer;
	
	private WorldState state;
	
	public Simulation(ApplicationConfiguration config) {
		this.config = config;
	}
	
	public void run() {
		this.running = true;
		preInit();
		if (DEBUG)
			GLHelper.initDebug();
		LOGGER.info("Starting " + NAME + " - " + VERSION);
		
		Display.createDisplay(NAME + " - " + VERSION, config);
		
		Waiter waiter = new Waiter();
		
		new Thread(() -> {
			try {
				init();
				waiter.unblock();
				while (running) {
					runLoop();
				}
			} catch (Throwable e) {
				LOGGER.catching(e);
				close();
			} finally {
				waiter.unblock();
			}
			shutdown();
		}, "Simulation Thread").start();
		
		try {
			waiter.await();
			LOGGER.info("Enable Event Listener");
			while (running) {
				Display.waitEvents();
				Thread.sleep(10);
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
			close();
		}
		Display.terminate();
	}
	
	private void preInit() {
		LOGGER.debug("PreInit");
		ImageIO.setUseCache(false);
		Locale.setDefault(Locale.ROOT);
	}
	
	private void init() {
		LOGGER.info("Init SimulationThread");
		Display.makeContextCurrent(config);
		
		LOGGER.info("LWJGL Version: {}", Version.getVersion());
		LOGGER.info("OpenGL Version: {}", GL11.glGetString(GL11.GL_VERSION));
		LOGGER.info("GLSL Version: {}", GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		
		GLUtils.init();
		Loader.init();
		renderer = new MasterRenderer();
		timer = new Timer();
		
		state = new WorldState();
	}
	
	private void runLoop() {
		timer.update(config.getTps());
		
		if (state.isAutospeed() && state.isRendering() && !state.isPaused()) {
			float ticksPerFrame = (config.getTps() / Math.max(config.getFps(), 10f));
			state.setSpeed(state.getSpeed() * Math.clamp(ticksPerFrame / (timer.getElapsedTicks() + timer.getPartialTicks()), 0.2f, 1.2f));
		}
		
		if (state.isAutospeed() && !state.isRendering()) {
			runTick();
		} else {
			for (int j = 0; j < Math.min(config.getTps() * 5, timer.getElapsedTicks()); j++) {
				runTick();
			}
		}
		
		Runnable task;
		while ((task = scheduledTasks.poll()) != null) {
			task.run();
		}
		
		if (state.isRendering())
			render();
		
		while (timer.updateDebugTime()) {
			LOGGER.debug("FPS: {}, TPS: {}, Events: {}, Steps: {}", fpsCounter.getAndSet(0), tpsCounter.getAndSet(0), eventsCounter.getAndSet(0), stepsCounter.getAndSet(0));
		}
		
		timer.sync(config.getFps());
		
		if (Display.shouldClose())
			close();
	}
	
	private void render() {
		fpsCounter.getAndIncrement();
		Display.updateDimensions();
		if (Display.pollResized())
			renderer.updateSize();
		renderer.render(state, timer);
		
		GLHelper.checkGLError();
		Display.swapBuffers();
	}
	
	private void runTick() {
		tpsCounter.getAndIncrement();
		int events = Display.Inputs.sendEvents(state);
		eventsCounter.getAndAdd(events);
		
		int steps = state.tick();
		stepsCounter.getAndAdd(steps);
	}
	
	@Override
	public void addTask(Runnable task) {
		scheduledTasks.add(task);
	}
	
	public void close() {
		running = false;
		GLFW.glfwPostEmptyEvent();
		LOGGER.debug("Marked for Stopping");
	}
	
	public void shutdown() {
		LOGGER.info("Stopped");
	}
}
