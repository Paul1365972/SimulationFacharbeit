package io.github.paul1365972.simulation.renderer.loader;

import io.github.paul1365972.simulation.renderer.utils.DataBuffer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import java.awt.Color;

public class RGVao {
	
	private int vaoId, vboId;
	private int vboSize;
	private DataBuffer buffer;
	
	private int count = 0;
	private int bufferEntries = 128;
	private int vertexCount;
	
	public RGVao() {
		vaoId = GL30.glGenVertexArrays();
		
		GL30.glBindVertexArray(vaoId);
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		
		float[] vertices = new float[] {-0.5f, -0.5f, 0, -0.5f, 0.5f, 0, 0.5f, 0.5f, 0, 0.5f, -0.5f, 0};
		float[] texcoords = new float[] {0, 1, 0, 0, 1, 0, 1, 1};
		int[] indices = new int[] {0, 1, 2, 0, 2, 3};
		
		vertexCount = indices.length;
		Loader.bindIndicesBuffer(indices);
		Loader.storeDataInAttributeList(0, vertices, 3);
		Loader.storeDataInAttributeList(1, texcoords, 2);
		
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		int offset = 5 * 4 * 0;
		GL30.glEnableVertexAttribArray(2);
		GL30.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, 5 * 4 * 4, 4 * 4 * 0 + offset);
		GL33.glVertexAttribDivisor(2, 1);
		GL30.glEnableVertexAttribArray(3);
		GL30.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, 5 * 4 * 4, 4 * 4 * 1 + offset);
		GL33.glVertexAttribDivisor(3, 1);
		GL30.glEnableVertexAttribArray(4);
		GL30.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 5 * 4 * 4, 4 * 4 * 2 + offset);
		GL33.glVertexAttribDivisor(4, 1);
		GL30.glEnableVertexAttribArray(5);
		GL30.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, 5 * 4 * 4, 4 * 4 * 3 + offset);
		GL33.glVertexAttribDivisor(5, 1);
		GL30.glEnableVertexAttribArray(6);
		GL30.glVertexAttribPointer(6, 4, GL11.GL_FLOAT, false, 5 * 4 * 4, 4 * 4 * 4 + offset);
		GL33.glVertexAttribDivisor(6, 1);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 5 * 4 + bufferEntries * (16 + 4) * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		buffer = DataBuffer.create(bufferEntries * (16 + 4) * 4);
		
		GL30.glBindVertexArray(0);
	}
	
	public void reset() {
		count = 0;
		buffer.clear();
	}
	
	private float[] tmp = new float[20];
	
	public void push(Matrix4f matrix, Color color) {
		count++;
		if (count > bufferEntries) {
			bufferEntries *= 2;
			buffer.resize(bufferEntries * (16 + 4) * 4, false);
		}
		color.getRGBComponents(tmp);
		matrix.get(tmp, 4);
		buffer.put(tmp);
	}
	
	public void upload() {
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		if (vboSize != buffer.bytes().capacity()) {
			vboSize = buffer.bytes().remaining();
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.bytes(), GL15.GL_STREAM_DRAW);
		} else {
			//GL15.nglBufferData(GL15.GL_ARRAY_BUFFER, buffer.bytes().remaining(), 0, GL15.GL_STREAM_DRAW);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer.bytes());
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void draw() {
		GL30.glBindVertexArray(vaoId);
		
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0, count);
		
		GL30.glBindVertexArray(0);
	}
	
}
