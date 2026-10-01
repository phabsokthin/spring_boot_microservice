package com.spring_microservice.order_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductResponse(
        @JsonProperty("productId")
        @JsonAlias({"id", "product_id"})
        Long productId,
        String productName,
        String description,
        BigDecimal price,
        Integer quantity,
        String sku,
        String status,
        Object createdAt,
        Object updatedAt
) {
}