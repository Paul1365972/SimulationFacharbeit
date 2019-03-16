package io.github.paul1365972.simulation.client.main;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GLCapabilities;

public class ApplicationConfiguration {
	
	private GLCapabilities capabilities;
	private GLFWVidMode vidMode;
	private int tps, fps;
	private boolean vsync;
	
	public ApplicationConfiguration() {
		this.tps = 100;
		this.fps = -1;
		this.vsync = false;
	}
	
	public int getTps() {
		return tps;
	}
	
	public void setTps(int tps) {
		this.tps = tps;
	}
	
	public int getFps() {
		return fps;
	}
	
	public void setFps(int fps) {
		this.fps = fps;
	}
	
	public boolean isVsync() {
		return vsync;
	}
	
	public void setVsync(boolean vsync) {
		this.vsync = vsync;
	}
	
	public GLCapabilities getCapabilities() {
		return capabilities;
	}
	
	public void setCapabilities(GLCapabilities capabilities) {
		this.capabilities = capabilities;
	}
	
	public GLFWVidMode getVidMode() {
		return vidMode;
	}
	
	public void setVidMode(GLFWVidMode vidMode) {
		this.vidMode = vidMode;
	}
}
