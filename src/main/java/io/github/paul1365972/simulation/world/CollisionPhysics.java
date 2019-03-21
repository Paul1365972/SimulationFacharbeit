package io.github.paul1365972.simulation.world;

import io.github.paul1365972.simulation.renderer.loader.RGVao;
import io.github.paul1365972.simulation.renderer.utils.MvpMatrix;
import org.joml.Vector2d;

import java.awt.Color;

public class CollisionPhysics implements Physics {
	
	@Override
	public void step(WorldState state) {
		// Get the time in seconds to simulate this step
		double deltaT = state.getConfig().getDeltaT();
		
		// Get the handler for detecting and resolving collisions
		InteractionHandler handler = state.getConfig().getHandler();
		
		// Let every Particle interact with each other
		for (int i = 0; i < state.getParticles().size(); i++) {
			for (int j = i + 1; j < state.getParticles().size(); j++) {
				Particle t = state.getParticles().get(i);
				Particle o = state.getParticles().get(j);
				handler.interact(t, o, deltaT);
			}
		}
		// Update positions and normalize all particles
		for (Particle p : state.getParticles()) {
			update(p, deltaT);
			normalize(p, state);
		}
	}
	
	private void update(Particle p, double deltaT) {
		// Applies velocity to position
		Vector2d dvel = p.vel.mul(deltaT, new Vector2d());
		p.pos.add(dvel);
	}
	
	private void normalize(Particle p, WorldState state) {
		// Let particle bounce of walls and also add momentum to them
		if (p.pos.x + p.radius >= state.getConfig().getWidth()) {
			state.addMomentumX(p.vel.x * p.mass * 2);
			p.vel.x *= -1;
		}
		if (p.pos.x - p.radius < -state.getConfig().getWidth()) {
			state.addMomentumX(p.vel.x * p.mass * 2);
			p.vel.x *= -1;
		}
		if (p.pos.y + p.radius >= state.getConfig().getHeight()) {
			state.addMomentumY(p.vel.y * p.mass * 2);
			p.vel.y *= -1;
		}
		if (p.pos.y - p.radius < -state.getConfig().getHeight()) {
			state.addMomentumY(p.vel.y * p.mass * 2);
			p.vel.y *= -1;
		}
	}
	
	@Override
	public void render(WorldState state, RGVao particleVao, MvpMatrix mvpMatrix) {
		for (Particle p : state.getParticles()) {
			// Draw Particle
			mvpMatrix.setModel((float) p.pos.x, (float) p.pos.y, 0, (float) p.radius * 2, (float) p.radius * 2, 0);
			particleVao.push(mvpMatrix.get(), Color.RED);
		}
	}
	
}
