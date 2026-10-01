package com.spring_microservice.order_service.client;
import com.spring_microservice.order_service.dto.ClientResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "client-service"
)
public interface ClientClient {

    @GetMapping("/api/clients/{id}")
    ClientResponse getClient(
            @PathVariable("id") String id
    );

    
}