package io.github.paul1365972.simulation.world;

import io.github.paul1365972.simulation.client.io.EventListener;
import io.github.paul1365972.simulation.client.io.InputEvent;
import io.github.paul1365972.simulation.renderer.utils.MvpMatrix;
import io.github.paul1365972.simulation.util.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.util.List;

public class WorldState implements EventListener {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final DecimalFormat df = new DecimalFormat("0.0000000");
	
	private boolean drag = false;
	private InputEvent.CursorPos lastCursor;
	private MvpMatrix mvpMatrix = new MvpMatrix();
	private float translateX, translateY, scale = 0.1f;
	private boolean rendering = true, paused = false, autospeed = false;
	private float speed = 1, left = 0;
	
	private Config config;
	private Physics physics;
	private List<Particle> particles;
	private double momentumX, momentumY;
	
	public WorldState() {
		reloadConfig();
		
		physics = new CollisionPhysics();
		physics.init(this);
	}
	
	public void reloadConfig() {
		config = Config.loadConfig();
		particles = config.getParticles();
		speed = 1 / config.getDeltaT();
		Interactions.COS_1D = Interactions.COS_2D = config.getValue();
		momentumX = momentumY = 0;
		printParticles();
	}
	
	public int tick() {
		int steps = 0;
		if (!paused) {
			for (left += speed / 100; left > 0; left--) {
				steps++;
				physics.tick(this);
			}
		}
		return steps;
	}
	
	private void printParticles() {
		float totalEnergy = 0;
		Vector2d totalMomentum = new Vector2d(momentumX, momentumY);
		for (Particle p : particles) {
			totalEnergy += p.vel.lengthSquared() * p.mass;
			Vector2d momentum = p.vel.mul(p.mass, new Vector2d());
			totalMomentum.add(momentum);
		}
		
		LOGGER.info("All Particles Total-Energy: " + df.format(totalEnergy) + " in J; Total-Momentum: " + df.format(totalMomentum.length()));
		LOGGER.info("Wall-Momentum: " + new Vector2d(momentumX, momentumY).toString(df));
		/*
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			LOGGER.info("Particle #" + i + " Mass: " + p.radius + " in kg; Radius: " + p.radius + " in m");
			LOGGER.info("            Position: " + p.pos.toString(df) + " in m");
			LOGGER.info("            Velocity: " + p.vel.toString(df) + " in m/s");
			LOGGER.info("            Energy: " + df.format(p.vel.lengthSquared() * p.mass) + " in J");
			LOGGER.info("            Momentum: " + df.format(p.vel.length() * p.mass) + " in kg*m/s");
		}*/
	}
	
	private void printParticle(Particle p) {
		LOGGER.info("Particle #" + particles.indexOf(p) + " Mass: " + p.radius + " in kg; Radius: " + p.radius + " in m");
		LOGGER.info("            Position: " + p.pos.toString(df) + " in m");
		LOGGER.info("            Velocity: " + p.vel.toString(df) + " in m/s");
		LOGGER.info("            Energy: " + df.format(p.vel.lengthSquared() * p.mass) + " in J");
		LOGGER.info("            Momentum: " + df.format(p.vel.length() * p.mass) + " in kg*m/s");
	}
	
	
	@Override
	public void onKey(InputEvent.Key e) {
		if (e.isPressed()) {
			switch (e.getKey()) {
				case GLFW.GLFW_KEY_UP:
					speed *= e.isShift() ? 1.25 : 2;
					LOGGER.info("Speed set to " + speed);
					break;
				case GLFW.GLFW_KEY_DOWN:
					speed /= e.isShift() ? 1.25 : 2;
					LOGGER.info("Speed set to " + speed);
					break;
				case GLFW.GLFW_KEY_N:
					rendering = !rendering;
					LOGGER.info(rendering ? "Rendering Enabled" : "Rendering Disabled");
					break;
				case GLFW.GLFW_KEY_A:
					autospeed = !autospeed;
					if (!autospeed)
						speed = 1 / config.getDeltaT();
					LOGGER.info(autospeed ? "Auto-Speed Enabled" : "Auto-Speed Disabled");
					break;
				case GLFW.GLFW_KEY_R:
					reloadConfig();
					break;
				case GLFW.GLFW_KEY_D:
					printParticles();
					break;
				case GLFW.GLFW_KEY_SPACE:
					paused = !paused;
					LOGGER.info(paused ? "Simulation Paused" : "Simulation Resumed");
					break;
			}
		}
	}
	
	@Override
	public void onScroll(InputEvent.Scroll e, InputEvent.CursorPos cursor) {
		float goalX = (float) cursor.getNdcX();
		float goalY = (float) cursor.getNdcY();
		mvpMatrix.setProjectionView(translateX, translateY, 0, scale, scale);
		Vector4f old = new Vector4f(goalX, goalY, 0, 1).mul(mvpMatrix.get().invert(new Matrix4f()));
		
		scale *= Math.pow(1.2, e.getYOffset());
		
		mvpMatrix.setProjectionView(translateX, translateY, 0, scale, scale);
		Vector4f now = new Vector4f(goalX, goalY, 0, 1).mul(mvpMatrix.get().invert(new Matrix4f()));
		
		translateX += (old.x - now.x) * scale;
		translateY += (old.y - now.y) * scale;
	}
	
	@Override
	public void onMouse(InputEvent.MouseButton e, InputEvent.CursorPos cursor) {
		if (e.isRightClick()) {
			if (e.isPressed()) {
				drag = true;
				lastCursor = cursor;
			} else {
				drag = false;
			}
		}
		if (e.isPressed()) {
			mvpMatrix.setProjectionView(translateX, translateY, 0, scale, scale);
			Vector4f point = new Vector4f((float) cursor.getNdcX(), (float) cursor.getNdcY(), 0, 1).mul(mvpMatrix.get().invert(new Matrix4f()));
			float minDist = Float.MAX_VALUE;
			Particle nearest = null;
			for (Particle p : particles) {
				double dx = point.x - p.pos.x;
				double dy = point.y - p.pos.y;
				float distSq = (float) (dx * dx + dy * dy);
				if (distSq <= minDist && distSq < p.radius * p.radius) {
					minDist = distSq;
					nearest = p;
				}
			}
			if (nearest != null) {
				if (e.isLeftClick())
					printParticle(nearest);
				else if (e.isMiddleClick())
					particles.remove(nearest);
			}
		}
	}
	
	@Override
	public void onCursor(InputEvent.CursorPos cursor) {
		if (drag) {
			mvpMatrix.setProjectionView(translateX, translateY, 0, scale, scale);
			Vector4f old = new Vector4f((float) lastCursor.getNdcX(), (float) lastCursor.getNdcY(), 0, 1).mul(mvpMatrix.get().invert(new Matrix4f()));
			Vector4f now = new Vector4f((float) cursor.getNdcX(), (float) cursor.getNdcY(), 0, 1).mul(mvpMatrix.get().invert(new Matrix4f()));
			
			translateX += (old.x - now.x) * scale;
			translateY += (old.y - now.y) * scale;
			lastCursor = cursor;
		}
	}
	
	public Config getConfig() {
		return config;
	}
	
	public Physics getPhysics() {
		return physics;
	}
	
	public List<Particle> getParticles() {
		return particles;
	}
	
	public double getMomentumX() {
		return momentumX;
	}
	
	public void addMomentumX(double momentumX) {
		this.momentumX += momentumX;
	}
	
	public double getMomentumY() {
		return momentumY;
	}
	
	public void addMomentumY(double momentumY) {
		this.momentumY += momentumY;
	}
	
	public float getTranslateX() {
		return translateX;
	}
	
	public float getTranslateY() {
		return translateY;
	}
	
	public float getScale() {
		return scale;
	}
	
	public boolean isRendering() {
		return rendering;
	}
	
	public boolean isAutospeed() {
		return autospeed;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
