package com.spring_microservice.order_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    private String orderNumber;

    @JsonProperty("clientId")
    @JsonAlias({"client_id", "customerId", "customer_id"})
    private String clientId;

    @JsonProperty("productId")
    @JsonAlias({"product_id"})
    private Long productId;

    private ClientResponse client;
    private ProductResponse product;
    private BigDecimal totalAmount;
    private String status;
    private String customerName;
    private String customerEmail;

    public OrderRequest() {}

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public ClientResponse getClient() { return client; }
    public ProductResponse getProduct() { return product; }

    @JsonProperty("client")
    public void setClientNode(JsonNode node) {
        if (node == null || node.isNull()) return;

        if (node.isTextual()) {
            this.clientId = node.asText();
        } else if (node.isObject()) {
            if (node.hasNonNull("id")) {
                this.clientId = node.get("id").asText();
            }
            String clientCode = node.hasNonNull("clientCode") ? node.get("clientCode").asText() : null;
            String clientName = node.hasNonNull("clientName") ? node.get("clientName").asText() : null;
            String email = node.hasNonNull("email") ? node.get("email").asText() : null;
            String phone = node.hasNonNull("phone") ? node.get("phone").asText() : null;
            String address = node.hasNonNull("address") ? node.get("address").asText() : null;
            this.client = new ClientResponse(this.clientId, clientCode, clientName, email, phone, address);
        }
    }

    @JsonProperty("product")
    public void setProductNode(JsonNode node) {
        if (node == null || node.isNull()) return;

        if (node.isNumber()) {
            this.productId = node.asLong();
        } else if (node.isTextual()) {
            try {
                this.productId = Long.valueOf(node.asText());
            } catch (NumberFormatException ignored) {}
        } else if (node.isObject()) {
            if (node.hasNonNull("productId")) {
                this.productId = node.get("productId").asLong();
            } else if (node.hasNonNull("id")) {
                this.productId = node.get("id").asLong();
            }
        }
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getEffectiveClientId() {
        if (clientId != null && !clientId.isBlank()) return clientId.trim();
        if (client != null && client.id() != null && !client.id().isBlank()) return client.id().trim();
        return null;
    }

    public Long getEffectiveProductId() {
        return productId;
    }
}
