package io.github.paul1365972.simulation.world;

import org.joml.Vector2d;

public class Particle {
	
	public Vector2d pos, vel;
	public double radius, mass;
	
	public Particle(double x, double y, double vx, double vy, double radius, double mass) {
		this.pos = new Vector2d(x, y);
		this.vel = new Vector2d(vx, vy);
		this.radius = radius;
		this.mass = mass;
	}
	
}
