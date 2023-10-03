package com.proyecto.investgo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaFactoringBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaFactoringBackendApplication.class, args);
	}
}
