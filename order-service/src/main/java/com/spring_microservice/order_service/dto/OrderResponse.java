package com.spring_microservice.order_service.dto;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        String orderNumber,
        ClientResponse client,
        ProductResponse product,
        BigDecimal totalAmount,
        String status
) {
}