package com.spring_microservice.order_service.service;

import com.spring_microservice.order_service.dto.ClientResponse;
import com.spring_microservice.order_service.dto.OrderRequest;
import com.spring_microservice.order_service.dto.OrderResponse;
import com.spring_microservice.order_service.dto.ProductResponse;
import com.spring_microservice.order_service.entity.Order;
import com.spring_microservice.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

        private final OrderRepository orderRepository;
        private final OrderClientService orderClientService;

        public OrderService(OrderRepository orderRepository, OrderClientService orderClientService) {
                this.orderRepository = orderRepository;
                this.orderClientService = orderClientService;
        }

        // =========================================================================
        // CREATE
        // =========================================================================

        @Transactional
        public OrderResponse createOrder(OrderRequest request) {
                validateUniqueOrderNumber(request.getOrderNumber(), null);

                String clientId = request.getEffectiveClientId();
                ClientResponse client = fetchClient(clientId);

                Long productId = request.getEffectiveProductId();
                ProductResponse product = fetchProduct(productId);

                Order order = new Order();
                order.setOrderNumber(request.getOrderNumber());
                order.setClientId(clientId);
                order.setProductId(productId);

                // If totalAmount is not specified but product price is available, default to
                // product price
                BigDecimal totalAmount = request.getTotalAmount();
                if (totalAmount == null && product != null && product.price() != null) {
                        totalAmount = product.price();
                }
                order.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
                order.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");

                populateCustomerInfo(order, client, request);

                Order savedOrder = orderRepository.save(order);
                return mapToOrderResponse(savedOrder, client, product);
        }

        // =========================================================================
        // READ (ALL & ONE)
        // =========================================================================

        @Transactional(readOnly = true)
        public List<OrderResponse> getAll() {
                return orderRepository.findAll().stream()
                                .map(this::mapToOrderResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public OrderResponse getOrderById(Long id) {
                Order order = findOrderById(id);
                return mapToOrderResponse(order);
        }

        // =========================================================================
        // UPDATE
        // =========================================================================

        @Transactional
        public OrderResponse updateOrder(Long id, OrderRequest request) {
                Order order = findOrderById(id);

                if (request.getOrderNumber() != null) {
                        validateUniqueOrderNumber(request.getOrderNumber(), order.getOrderNumber());
                        order.setOrderNumber(request.getOrderNumber());
                }

                String clientId = request.getEffectiveClientId();
                if (clientId != null && !clientId.isBlank()) {
                        order.setClientId(clientId);
                }

                Long productId = request.getEffectiveProductId();
                if (productId != null) {
                        order.setProductId(productId);
                }

                if (request.getTotalAmount() != null) {
                        order.setTotalAmount(request.getTotalAmount());
                }

                if (request.getStatus() != null) {
                        order.setStatus(request.getStatus());
                }

                ClientResponse client = fetchClient(order.getClientId());
                populateCustomerInfo(order, client, request);

                ProductResponse product = fetchProduct(order.getProductId());

                Order updatedOrder = orderRepository.save(order);
                return mapToOrderResponse(updatedOrder, client, product);
        }

        // =========================================================================
        // DELETE
        // =========================================================================

        @Transactional
        public void deleteOrder(Long id) {
                Order order = findOrderById(id);
                orderRepository.delete(order);
        }

        // =========================================================================
        // HELPER METHODS
        // =========================================================================

        private Order findOrderById(Long id) {
                return orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        }

        private void validateUniqueOrderNumber(String newOrderNumber, String existingOrderNumber) {
                if (newOrderNumber == null)
                        return;
                if (!newOrderNumber.equals(existingOrderNumber)
                                && orderRepository.existsByOrderNumber(newOrderNumber)) {
                        throw new RuntimeException("Order number already exists: " + newOrderNumber);
                }
        }

        private ClientResponse fetchClient(String clientId) {
                if (clientId == null || clientId.isBlank()) {
                        return null;
                }
                return orderClientService.getClient(clientId);
        }

        private ProductResponse fetchProduct(Long productId) {
                if (productId == null) {
                        return null;
                }
                return orderClientService.getProduct(productId);
        }

        private void populateCustomerInfo(Order order, ClientResponse client, OrderRequest request) {
                if (client != null && client.clientName() != null) {
                        order.setCustomerName(client.clientName());
                        order.setCustomerEmail(client.email());
                } else {
                        if (request.getCustomerName() != null)
                                order.setCustomerName(request.getCustomerName());
                        if (request.getCustomerEmail() != null)
                                order.setCustomerEmail(request.getCustomerEmail());
                }
        }

        private OrderResponse mapToOrderResponse(Order order) {
                ClientResponse client = fetchClient(order.getClientId());
                ProductResponse product = fetchProduct(order.getProductId());
                return mapToOrderResponse(order, client, product);
        }

        private OrderResponse mapToOrderResponse(Order order, ClientResponse client, ProductResponse product) {
                return new OrderResponse(
                                order.getId(),
                                order.getOrderNumber(),
                                client,
                                product,
                                order.getTotalAmount(),
                                order.getStatus());
        }
}
