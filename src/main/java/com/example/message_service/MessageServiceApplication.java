package com.example.message_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching // GỢI Ý 1: Bật cơ chế caching của Spring
@EnableAsync   // GỢI Ý: Bật cơ chế bất đồng bộ, sẽ cần cho các service khác
public class MessageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageServiceApplication.class, args);
	}

}
