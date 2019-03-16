package io.github.paul1365972.simulation.world;

import io.github.paul1365972.simulation.renderer.loader.RGVao;
import io.github.paul1365972.simulation.renderer.utils.MvpMatrix;

public interface Physics {
	
	void init(WorldState state);
	
	void tick(WorldState state);
	
	void render(WorldState state, RGVao particleVao, MvpMatrix mvpMatrix);
	
}
