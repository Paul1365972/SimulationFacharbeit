package io.github.paul1365972.simulation.renderer.loader;

public class Model {
	
	private final int vaoID;
	private final int vertexCount;
	
	public Model(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
}
