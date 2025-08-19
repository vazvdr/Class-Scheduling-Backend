package com.class_project.backend_class;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.github.cdimascio.dotenv.Dotenv;

@EnableCaching
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
				Dotenv dotenv = Dotenv.load();
				dotenv.entries().forEach(entry ->
					System.setProperty(entry.getKey(), entry.getValue())
				);
		SpringApplication.run(DemoApplication.class, args);
	}

}
