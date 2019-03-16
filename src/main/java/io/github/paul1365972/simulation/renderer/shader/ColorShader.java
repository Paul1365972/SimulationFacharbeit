package io.github.paul1365972.simulation.renderer.shader;

import org.joml.Matrix4f;

import java.awt.Color;

public class ColorShader extends AbstractShader {
	
	private int mvpMatrix, color;
	
	public ColorShader() {
		super("colorshader.txt");
	}
	
	public void loadMvpMatrix(Matrix4f matrix) {
		super.loadMatrix(mvpMatrix, matrix);
	}
	
	public void loadColor(Color c) {
		super.loadVector(color, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
	}
	
	@Override
	protected void getAllUniformLocations() {
		mvpMatrix = super.getUniformLocation("mvpMatrix");
		color = super.getUniformLocation("color");
	}
	
	@Override
	protected void connectTextureUnits() {
	}
}
