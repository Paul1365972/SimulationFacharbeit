package io.github.paul1365972.simulation.renderer.shader;

public class PPShader extends AbstractShader {
	
	public PPShader() {
		super("ppshader.txt");
	}
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0,"position");
		bindAttribute(1,"texCoordsIn");
	}
	
	@Override
	protected void getAllUniformLocations() {
	}
	
	@Override
	protected void connectTextureUnits() {
	}
}
