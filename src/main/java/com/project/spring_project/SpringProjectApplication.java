package com.project.spring_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringProjectApplication implements CommandLineRunner{
	@Autowired
	private Environment env;

	private static final Logger logger = LoggerFactory.getLogger(SpringProjectApplication.class);

	/**
	 * Main method to run the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpringProjectApplication.class, args);

	}

	/**
	 * Runs after the application context is loaded and ready to be used.
	 * Displays system information and application properties.
	 */
	@Override
	public void run(String... args) {
		logger.info("==== App Started ====");
		logger.info("Java Version: {}", System.getProperty("java.version"));
		logger.info("Current Time: {}", LocalDateTime.now());
		logger.info("OS: {}", System.getProperty("os.name"));
		logger.info("OS Version: {}", System.getProperty("os.version"));
		logger.info("Architecture: {}", System.getProperty("os.arch"));
		logger.info("JVM Max Memory: {} MB", Runtime.getRuntime().maxMemory() / (1024 * 1024));
		logger.info("JVM Available Processors: {}", Runtime.getRuntime().availableProcessors());
		logger.info("Spring Boot Version: {}", SpringBootVersion.getVersion());
		logger.info("Active Profiles: {}", env.getActiveProfiles().length > 0 ?
				String.join(", ", env.getActiveProfiles()) : "none");
		logger.info("==== App Ready! ====");
	}

}
