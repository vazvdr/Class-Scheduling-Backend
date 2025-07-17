package com.class_project.backend_class;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		
		System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
        System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("MAIL_SMTP_AUTH", dotenv.get("MAIL_SMTP_AUTH"));
        System.setProperty("MAIL_SMTP_STARTTLS_ENABLE", dotenv.get("MAIL_SMTP_STARTTLS_ENABLE"));
        System.setProperty("MAIL_SMTP_STARTTLS_REQUIRED", dotenv.get("MAIL_SMTP_STARTTLS_REQUIRED"));
        System.setProperty("MAIL_SMTP_SSL_TRUST", dotenv.get("MAIL_SMTP_SSL_TRUST"));
	    
		SpringApplication.run(DemoApplication.class, args);
	}

}
