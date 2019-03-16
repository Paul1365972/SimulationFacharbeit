package io.github.paul1365972.simulation.renderer.loader;

import org.lwjgl.opengl.GL11;

public class Texture {
	
	private final int id;
	
	public Texture(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void delete() {
		GL11.glDeleteTextures(id);
	}
}
