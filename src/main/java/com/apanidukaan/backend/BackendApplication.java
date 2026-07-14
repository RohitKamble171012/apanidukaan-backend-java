package com.apanidukaan.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner printMongoUri(@Value("${spring.data.mongodb.uri}") String mongoUri) {
		return args -> {
			System.out.println("=====================================");
			System.out.println("RESOLVED MONGO URI: " + mongoUri);
			System.out.println("=====================================");
		};
	}
}