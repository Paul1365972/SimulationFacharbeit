package io.github.paul1365972.simulation.renderer.loader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;

public class ColorModel extends Model {
	
	private final int vboID;
	
	private FloatBuffer buffer;
	
	public ColorModel(int vaoID, int vertexCount, int vboID) {
		super(vaoID, vertexCount);
		this.vboID = vboID;
	}
	
	public ColorModel(Model model, int vboID) {
		super(model.getVaoID(), model.getVertexCount());
		this.vboID = vboID;
	}
	
	public boolean ensureCapacity(int cap) {
		if (buffer == null || buffer.capacity() != cap) {
			buffer = BufferUtils.createFloatBuffer(cap);
			return true;
		}
		return false;
	}
	
	public FloatBuffer getBuffer() {
		return buffer;
	}
	
	public void upload() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public int getVboID() {
		return vboID;
	}
}
