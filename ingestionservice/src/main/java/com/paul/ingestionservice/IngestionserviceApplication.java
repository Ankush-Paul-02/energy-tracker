package com.paul.ingestionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IngestionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestionserviceApplication.class, args);
	}

}
