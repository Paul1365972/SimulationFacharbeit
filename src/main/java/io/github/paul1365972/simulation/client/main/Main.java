package io.github.paul1365972.simulation.client.main;

import io.github.paul1365972.simulation.client.Simulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

public class Main {
	
	public static void main(String[] args) {
		LogManager.getRootLogger();
		Logger LOGGER = LogManager.getLogger();
		String processId = ManagementFactory.getRuntimeMXBean().getName();
		LOGGER.info("Process ID: " + processId.substring(0, processId.indexOf('@')));
		LOGGER.info("Parsing arguments: " + Arrays.toString(args));
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread.currentThread().setName("Main Thread");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LOGGER.info("Exit");
		}, "Client Shutdown Thread"));
		
		ApplicationConfiguration ac = new ApplicationConfiguration();
		new Simulation(ac).run();
	}
}
