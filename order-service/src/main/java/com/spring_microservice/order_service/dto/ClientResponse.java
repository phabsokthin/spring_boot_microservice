package com.spring_microservice.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientResponse(
        String id,
        String clientCode,
        String clientName,
        String email,
        String phone,
        String address
) {
}