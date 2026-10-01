package com.spring_microservice.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients // Enable Feign Client for making HTTP requests to other microservices

// @EnableFeignClients(
//         basePackages =
//                 "com.spring_microservice.order_service.client"
// )
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
