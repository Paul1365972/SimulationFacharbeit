package io.github.paul1365972.simulation.renderer.utils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class GLUtils {
	
	public static void init() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		GL13.glEnable(GL13.GL_BLEND);
		GL13.glBlendFunc(GL13.GL_SRC_ALPHA, GL13.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		GL13.glDisable(GL13.GL_CULL_FACE);
		/*GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glCullFace(GL11.GL_BACK);*/
		
		//GL43.glDebugMessageControl(GL43.GL_DEBUG_SOURCE_API, GL43.GL_DEBUG_TYPE_OTHER, GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (int[]) null, false);
	}
	
	public static void clear(boolean accum, boolean color, boolean depth, boolean stencil) {
		int mask = accum ? GL11.GL_ACCUM_BUFFER_BIT : 0;
		mask |= color ? GL11.GL_COLOR_BUFFER_BIT : 0;
		mask |= depth ? GL11.GL_DEPTH_BUFFER_BIT : 0;
		mask |= stencil ? GL11.GL_STENCIL_BUFFER_BIT : 0;
		GL11.glClear(mask);
	}
}
