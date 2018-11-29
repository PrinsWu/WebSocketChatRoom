package com.prins.wschatroom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class WebApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("Application running!");
		log.error("Application running! error");
		log.warn("Application running! warn");
		log.info("Application running! info");
		log.debug("Application running! debug");
	}
}
