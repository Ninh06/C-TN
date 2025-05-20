package com.example.cdtn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CdtnApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdtnApplication.class, args);
	}

}
