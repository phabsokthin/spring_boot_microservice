package com.spring_microservice.order_service.service;

import com.spring_microservice.order_service.client.ClientClient;
import com.spring_microservice.order_service.client.ProductClient;
import com.spring_microservice.order_service.dto.ClientResponse;
import com.spring_microservice.order_service.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class OrderClientService {

    private final ClientClient clientClient;
    private final ProductClient productClient;

    public OrderClientService(
            ClientClient clientClient,
            ProductClient productClient) {
        this.clientClient = clientClient;
        this.productClient = productClient;
    }

    // =========================================================================
    // CLIENT SERVICE
    // =========================================================================

    @CircuitBreaker(name = "clientService", fallbackMethod = "clientFallback")
    public ClientResponse getClient(String clientId) {
        return clientClient.getClient(clientId);
    }

    public ClientResponse clientFallback(String clientId, Throwable throwable) {
        return new ClientResponse(
                clientId,
                null,
                "Client Service unavailable",
                null,
                null,
                null);
    }

    // =========================================================================
    // PRODUCT SERVICE
    // =========================================================================

    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public ProductResponse getProduct(Long productId) {
        return productClient.getProduct(productId);
    }

    public ProductResponse productFallback(Long productId, Throwable throwable) {
        return new ProductResponse(
                productId,
                "Product Service unavailable",
                "Product details are temporarily unavailable",
                null,
                null,
                null,
                "UNAVAILABLE",
                null,
                null);
    }
}