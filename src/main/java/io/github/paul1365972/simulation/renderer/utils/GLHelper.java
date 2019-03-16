package io.github.paul1365972.simulation.renderer.utils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Configuration;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class GLHelper {
	
	public static void checkGLError() {
		int error = GL11.glGetError();
		if (error != GL11.GL_NO_ERROR)
			throw new RuntimeException("GL Error: " + error);
	}
	
	public static void initDebug() {
		Configuration.DEBUG_STREAM.set(new PrintStream(new OutputStream() {
			private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
			
			private PrintStream out = System.out;
			private boolean newLine = true;
			
			private byte[] newLineBuffer = System.lineSeparator().getBytes(Charset.defaultCharset());
			private byte[] buffer = new byte[newLineBuffer.length];
			
			@Override
			public void write(int b) {
				if (newLine)
					out.print("[" + df.format(new Date()) + "] [" + Thread.currentThread().getName() + "/debug]: ");
				
				out.write(b);
				
				System.arraycopy(buffer, 1, buffer, 0, buffer.length - 1);
				buffer[buffer.length - 1] = (byte) b;
				newLine = Arrays.equals(buffer, newLineBuffer);
			}
			
			@Override
			public void flush() {
				out.flush();
			}
			
			@Override
			public void close() {
				out.close();
			}
		}, true));
		
		Configuration.DEBUG.set(true);
		Configuration.DEBUG_FUNCTIONS.set(false);
		Configuration.DEBUG_LOADER.set(false);
		
		Configuration.DEBUG_STACK.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR_INTERNAL.set(true);
	}
}
