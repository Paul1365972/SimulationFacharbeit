package io.github.paul1365972.simulation.util;

import io.github.paul1365972.simulation.world.InteractionHandler;
import io.github.paul1365972.simulation.world.Interactions;
import io.github.paul1365972.simulation.world.Particle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Config {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private float width = 10, height = 10, thickness = 2;
	private float deltaT = 0.001f;
	private float value = 0.5f;
	private InteractionHandler handler;
	private List<Particle> particles;
	
	public Config() {
		this.particles = new ArrayList<>();
	}
	
	public Config(float width, float height, float thickness, float deltaT, InteractionHandler handler, List<Particle> particles) {
		this.width = width;
		this.height = height;
		this.thickness = thickness;
		this.deltaT = deltaT;
		this.handler = handler;
		this.particles = particles;
	}
	
	public static Config loadConfig() {
		Config config = new Config();
		if (!new File("config.txt").exists()) {
			LOGGER.info("Creating config file");
			try {
				Files.copy(Config.class.getResourceAsStream("/default_config.txt"), new File("config.txt").toPath());
			} catch (IOException e) {
				LOGGER.catching(e);
			}
		}
		try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
			boolean particles = false;
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#"))
					continue;
				if (particles) {
					String[] args = line.split(";");
					config.particles.add(new Particle(Float.parseFloat(args[0]), Float.parseFloat(args[1]),
							Float.parseFloat(args[2]), Float.parseFloat(args[3]),
							Float.parseFloat(args[4]), Float.parseFloat(args[5])));
				} else {
					int index = line.indexOf('=');
					if (index == -1) {
						if (line.equalsIgnoreCase("particles"))
							particles = true;
					} else {
						String key = line.substring(0, index);
						String value = line.substring(index + 1);
						
						if (key.equalsIgnoreCase("width")) {
							config.width = Float.parseFloat(value);
						} else if (key.equalsIgnoreCase("height")) {
							config.height = Float.parseFloat(value);
						} else if (key.equalsIgnoreCase("thickness")) {
							config.thickness = Float.parseFloat(value);
						} else if (key.equalsIgnoreCase("deltat")) {
							config.deltaT = Float.parseFloat(value);
						} else if (key.equalsIgnoreCase("value")) {
							config.value = Float.parseFloat(value);
						} else if (key.equalsIgnoreCase("handler")) {
							if (value.equalsIgnoreCase("INELASTIC1D"))
								config.handler = Interactions.INELASTIC1D;
							else if (value.equalsIgnoreCase("INELASTIC2D"))
								config.handler = Interactions.INELASTIC2D;
							else if (value.equalsIgnoreCase("ELASTIC1D"))
								config.handler = Interactions.ELASTIC1D;
							else if (value.equalsIgnoreCase("ELASTIC2D"))
								config.handler = Interactions.ELASTIC2D;
							else if (value.equalsIgnoreCase("SEMIELASTIC1D"))
								config.handler = Interactions.SEMIELASTIC1D;
							else if (value.equalsIgnoreCase("SEMIELASTIC2D"))
								config.handler = Interactions.SEMIELASTIC2D;
							else if (value.equalsIgnoreCase("SOFT_CONST"))
								config.handler = Interactions.SOFT_CONST;
							else if (value.equalsIgnoreCase("SOFT_INV"))
								config.handler = Interactions.SOFT_INV;
						}
						
					}
				}
			}
		} catch (IOException e) {
			LOGGER.catching(e);
			LOGGER.info("Could not load File");
		}
		LOGGER.info("Updated to " + config);
		return config;
	}
	
	@Override
	public String toString() {
		return "Config{" +
				"width=" + width +
				", height=" + height +
				", thickness=" + thickness +
				", deltaT=" + deltaT +
				", value=" + value +
				", particles=" + particles.size() +
				'}';
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getThickness() {
		return thickness;
	}
	
	public float getDeltaT() {
		return deltaT;
	}
	
	public InteractionHandler getHandler() {
		return handler;
	}
	
	public List<Particle> getParticles() {
		return particles;
	}
	
	public float getValue() {
		return value;
	}
}
