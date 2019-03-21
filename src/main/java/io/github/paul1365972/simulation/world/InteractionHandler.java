package io.github.paul1365972.simulation.world;

@FunctionalInterface
public interface InteractionHandler {
	void interact(Particle t, Particle o, double deltaT);
}