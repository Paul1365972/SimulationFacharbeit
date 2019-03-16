package io.github.paul1365972.simulation.renderer.shader;

public class PPShader extends AbstractShader {
	
	private int blurEnable;
	private int texelStep;
	
	public PPShader() {
		super("ppshader.txt");
	}
	
	public void loadBlurEnable(boolean state) {
		super.loadInt(blurEnable, state ? 1 : 0);
	}
	
	public void loadTexelStep(float x, float y) {
		super.loadVector(texelStep, x, y);
	}
	
	@Override
	protected void getAllUniformLocations() {
		blurEnable = super.getUniformLocation("blurEnable");
		texelStep = super.getUniformLocation("texelStep");
	}
	
	@Override
	protected void connectTextureUnits() {
	}
}
