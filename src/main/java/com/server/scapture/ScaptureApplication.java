package com.server.scapture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ScaptureApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScaptureApplication.class, args);
	}

}
