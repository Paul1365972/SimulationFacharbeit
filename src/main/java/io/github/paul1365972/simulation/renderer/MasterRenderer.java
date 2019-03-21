package io.github.paul1365972.simulation.renderer;

import io.github.paul1365972.simulation.client.io.Display;
import io.github.paul1365972.simulation.client.util.Timer;
import io.github.paul1365972.simulation.renderer.fbo.DefaultFbo;
import io.github.paul1365972.simulation.renderer.fbo.ViewFbo;
import io.github.paul1365972.simulation.renderer.loader.Loader;
import io.github.paul1365972.simulation.renderer.loader.RGVao;
import io.github.paul1365972.simulation.renderer.shader.ColorShader;
import io.github.paul1365972.simulation.renderer.shader.PPShader;
import io.github.paul1365972.simulation.renderer.shader.ParticleShader;
import io.github.paul1365972.simulation.renderer.utils.GLUtils;
import io.github.paul1365972.simulation.renderer.utils.MvpMatrix;
import io.github.paul1365972.simulation.world.WorldState;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.Color;

public class MasterRenderer {
	
	private PPShader ppShader;
	private ColorShader colorShader;
	private ParticleShader particleShader;
	
	private RGVao paricleVao;
	
	private DefaultFbo defaultFbo;
	private ViewFbo viewFbo;
	
	private MvpMatrix mvpMatrix = new MvpMatrix();
	
	public MasterRenderer() {
		ppShader = new PPShader();
		colorShader = new ColorShader();
		particleShader = new ParticleShader();
		paricleVao = new RGVao();
		
		defaultFbo = new DefaultFbo(Display.getFramebufferWidth(), Display.getFramebufferHeight());
		viewFbo = new ViewFbo(Display.getViewFramebufferWidth(), Display.getViewWindowHeight());
	}
	
	public void render(WorldState state, Timer timer) {
		mvpMatrix.setProjectionView(state.getTranslateX(), state.getTranslateY(), 0, state.getScale(), state.getScale());
		
		viewFbo.bindFrameBuffer();
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLUtils.clear(false, true, true, false);
		
		renderParticles(state);
		renderBorder(state);
		
		defaultFbo.bindFrameBuffer();
		GL11.glClearColor(0.125f, 0.125f, 0.125f, 1.0f);
		GLUtils.clear(false, true, true, false);
		int cx = (Display.getFramebufferWidth() - Display.getViewFramebufferWidth()) / 2;
		int cy = (Display.getFramebufferHeight() - Display.getViewFramebufferHeight()) / 2;
		GL11.glViewport(cx, cy, Display.getViewFramebufferWidth(), Display.getViewFramebufferHeight());
		
		ppShader.start();
		
		GL30.glBindVertexArray(Loader.SCREEN.getVaoID());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, viewFbo.getColorTexture(0).getId());
		GL11.glDrawElements(GL11.GL_TRIANGLES, Loader.SCREEN.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);
		
		ppShader.stop();
	}
	
	private void renderBorder(WorldState state) {
		float thickness = state.getConfig().getThickness();
		colorShader.start();
		GL30.glBindVertexArray(Loader.QUAD.getVaoID());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.RECT.getId());
		colorShader.loadColor(new Color(0.25f, 0.25f, 0.25f));
		
		mvpMatrix.setModel(state.getConfig().getWidth() + thickness / 2, 0, 0, thickness, (state.getConfig().getHeight() + thickness) * 2, 0);
		colorShader.loadMvpMatrix(mvpMatrix.get());
		GL11.glDrawElements(GL11.GL_TRIANGLES, Loader.QUAD.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		mvpMatrix.setModel(-state.getConfig().getWidth() - thickness / 2, 0, 0, thickness, (state.getConfig().getHeight() + thickness) * 2, 0);
		colorShader.loadMvpMatrix(mvpMatrix.get());
		GL11.glDrawElements(GL11.GL_TRIANGLES, Loader.QUAD.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		mvpMatrix.setModel(0, state.getConfig().getHeight() + thickness / 2, 0, (state.getConfig().getWidth() + thickness) * 2, thickness, 0);
		colorShader.loadMvpMatrix(mvpMatrix.get());
		GL11.glDrawElements(GL11.GL_TRIANGLES, Loader.QUAD.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		mvpMatrix.setModel(0, -state.getConfig().getHeight() - thickness / 2, 0, (state.getConfig().getWidth() + thickness) * 2, thickness, 0);
		colorShader.loadMvpMatrix(mvpMatrix.get());
		GL11.glDrawElements(GL11.GL_TRIANGLES, Loader.QUAD.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);
		colorShader.stop();
	}
	
	private void renderParticles(WorldState state) {
		/*List<Particle> toRender = new ArrayList<>(1000);
		mvpMatrix.resetModel();
		Matrix4f inverse = mvpMatrix.get().invert(new Matrix4f());
		Vector4f leftdown = new Vector4f(-1.1f, -1.1f, 0, 1).mul(inverse);
		Vector4f upright = new Vector4f(1.1f, 1.1f, 0, 1).mul(inverse);*/
		
		paricleVao.reset();
		state.getPhysics().render(state, paricleVao, mvpMatrix);
		paricleVao.upload();
		
		particleShader.start();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.CIRCLE.getId());
		paricleVao.draw();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		particleShader.stop();
	}
	
	public void updateSize() {
		defaultFbo.update(Display.getFramebufferWidth(), Display.getFramebufferHeight());
		viewFbo.update(Display.getViewFramebufferWidth(), Display.getViewWindowHeight());
	}
	
	public void cleanUp() {
		defaultFbo.cleanUp();
		viewFbo.cleanUp();
	}
}
