package io.github.paul1365972.simulation.renderer.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Loader {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Texture INVALID_TEXTURE;
	
	public static Texture CIRCLE, RECT;
	public static Model QUAD, SCREEN;
	
	public static void init() {
		LOGGER.info("Loading Resources");
		initInvalidTexture(2, 2);
		
		CIRCLE = uploadMipmaps("circle", 256);
		RECT = uploadTexture((ByteBuffer) BufferUtils.createByteBuffer(4).putInt(0xFFFFFFFF).flip(), 1, 1);
		QUAD = loadModel(new float[] {-0.5f, -0.5f, 0, -0.5f, 0.5f, 0, 0.5f, 0.5f, 0, 0.5f, -0.5f, 0},
				new float[] {0, 1, 0, 0, 1, 0, 1, 1}, new int[] {0, 1, 2, 0, 2, 3});
		SCREEN = loadModel(new float[] {-1, -1, 0, -1, 1, 0, 1, 1, 0, 1, -1, 0},
				new float[] {0, 0, 0, 1, 1, 1, 1, 0}, new int[] {0, 1, 2, 0, 2, 3});
	}
	
	private static void initInvalidTexture(int width, int height) {
		if (width % 2 != 0 || height % 2 != 0)
			throw new IllegalArgumentException("Width and height must be even");
		
		BufferedImage invalidImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = invalidImage.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width / 2, height / 2);
		g.fillRect(width / 2, height / 2, width, height);
		g.setColor(Color.MAGENTA);
		g.fillRect(0, width / 2, width / 2, height);
		g.fillRect(width / 2, 0, width, height / 2);
		g.dispose();
		INVALID_TEXTURE = uploadTexture(invalidImage);
	}
	
	private static Texture loadTexture(String path) {
		path = "/textures/" + path;
		InputStream is = Loader.class.getResourceAsStream(path);
		if (is == null) {
			LOGGER.error("Unable to find Image: " + path);
			return INVALID_TEXTURE;
		}
		try {
			return uploadTexture(ImageIO.read(is));
		} catch (IOException e) {
			LOGGER.error("Unable to read Image", e);
			return INVALID_TEXTURE;
		}
	}
	
	public static Texture uploadTexture(BufferedImage bimg) {
		int[] pixels = bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), null, 0, bimg.getWidth());
		ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * 4);
		for (int pixel : pixels) {
			buffer.put((byte) (pixel >> 16 & 0xFF)); // R
			buffer.put((byte) (pixel >> 8 & 0xFF)); // G
			buffer.put((byte) (pixel >> 0 & 0xFF)); // B
			buffer.put((byte) (pixel >> 24 & 0xFF)); // A
		}
		buffer.flip();
		return uploadTexture(buffer, bimg.getWidth(), bimg.getHeight());
	}
	
	private static Texture uploadTexture(ByteBuffer buffer, int width, int height) {
		if (Integer.bitCount(width) != 1 && Integer.bitCount(height) != 1)
			throw new IllegalArgumentException("Can only use Images with a size thats a power of 2");
		
		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		GL11.glGetError(); //TODO:
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		return new Texture(id);
	}
	
	private static Texture uploadMipmaps(String name, int start) {
		if (Integer.bitCount(start) != 1)
			throw new IllegalArgumentException("Can only use Images with a size thats a power of 2");
		
		name = "/textures/" + name;
		
		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		
		int max = 0;
		int size = start;
		for (int i = 0; size != 1; i++, size /= 2) {
			InputStream is = Loader.class.getResourceAsStream(name + size + ".png");
			if (is == null) {
				LOGGER.error("Unable to find Image: " + name + size + ".png");
				GL11.glDeleteTextures(id);
				return INVALID_TEXTURE;
			}
			BufferedImage bimg;
			try {
				bimg = ImageIO.read(is);
			} catch (IOException e) {
				LOGGER.error(e);
				GL11.glDeleteTextures(id);
				return INVALID_TEXTURE;
			}
			
			int[] pixels = bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), null, 0, bimg.getWidth());
			ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * 4);
			for (int pixel : pixels) {
				buffer.put((byte) (pixel >> 16 & 0xFF)); // R
				buffer.put((byte) (pixel >> 8 & 0xFF)); // G
				buffer.put((byte) (pixel >> 0 & 0xFF)); // B
				buffer.put((byte) (pixel >> 24 & 0xFF)); // A
			}
			buffer.flip();
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, GL11.GL_RGBA, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			max = i;
		}
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, max);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		return new Texture(id);
	}
	
	private static Model loadModel(float[] vertices, float[] texcoords, int[] indices) {
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, vertices, 3);
		storeDataInAttributeList(1, texcoords, 2);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, indices.length);
	}
	
	public static void bindIndicesBuffer(int[] indices) {
		IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
		intBuffer.put(indices);
		intBuffer.flip();
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
	}
	
	public static void storeDataInAttributeList(int attributeNumber, float[] data, int size) {
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
		floatBuffer.put(data);
		floatBuffer.flip();
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
}
