package io.github.paul1365972.simulation.renderer.fbo;

import io.github.paul1365972.simulation.renderer.loader.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFbo {
	
	private int frameBuffer;
	
	private int depthTexture;
	
	private int[] colorTexture;
	
	private int width, height;
	
	private List<Integer> deleteRenderbuffers = new ArrayList<>();
	private List<Integer> deleteTextures = new ArrayList<>();
	
	public AbstractFbo(int width, int height) {
		this.width = width;
		this.height = height;
		this.colorTexture = new int[32];
		
		createFrameBuffer();
		resize(width, height);
	}
	
	public AbstractFbo(int frameBuffer, int width, int height) {
		this.width = width;
		this.height = height;
		this.frameBuffer = frameBuffer;
	}
	
	public void update(int width, int height) {
		if (frameBuffer == 0) {
			this.width = width;
			this.height = height;
		} else {
			resize(width, height);
		}
	}
	
	private void resize(int width, int height) {
		this.width = width;
		this.height = height;
		bindFrameBuffer();
		
		deleteTextures();
		deleteRenderBuffers();
		
		onRedefine();
		
		check();
	}
	
	protected abstract void onRedefine();
	
	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}
	
	protected void addColorTexture2D(int colorAttachment, int magFilter, int minFilter) {
		colorTexture[colorAttachment] = GL11.glGenTextures();
		deleteTextures.add(colorTexture[colorAttachment]);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture[colorAttachment]);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glGetError(); //TODO:
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + colorAttachment, GL11.GL_TEXTURE_2D, colorTexture[colorAttachment], 0);
	}
	
	protected void addDepthTexture2D(int magFilter, int minFilter) {
		depthTexture = GL11.glGenTextures();
		deleteTextures.add(depthTexture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}
	
	protected void addDepthBuffer() {
		int depthBuffer = GL30.glGenRenderbuffers();
		deleteRenderbuffers.add(depthBuffer);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
	}
	
	protected void setDrawBuffers(int... drawBuffers) {
		if (drawBuffers.length == 0) {
			GL20.glDrawBuffers(GL11.GL_NONE);
		} else {
			IntBuffer buffer = BufferUtils.createIntBuffer(drawBuffers.length);
			buffer.put(drawBuffers);
			buffer.flip();
			GL20.glDrawBuffers(buffer);
		}
	}
	
	private void createFrameBuffer() {
		frameBuffer = GL30.glGenFramebuffers();
	}
	
	
	public Texture getColorTexture(int colorAttachment) {
		return new Texture(colorTexture[colorAttachment]);
	}
	
	public Texture getDepthTexture() {
		return new Texture(depthTexture);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private void deleteTextures() {
		for (Integer id : deleteTextures) {
			GL11.glDeleteTextures(id);
		}
	}
	
	private void deleteRenderBuffers() {
		for (Integer id : deleteRenderbuffers) {
			GL30.glDeleteRenderbuffers(id);
		}
	}
	
	public void cleanUp() {
		if (frameBuffer != 0) {
			GL30.glDeleteFramebuffers(frameBuffer);
			deleteRenderBuffers();
			deleteTextures();
		}
	}
	
	public AbstractFbo check() {
		int error = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (error != GL30.GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException("Framebuffer not complete error: " + error);
		return this;
	}
	
}
