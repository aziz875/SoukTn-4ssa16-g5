package com.example.orderservice.api.dto;

import com.example.orderservice.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String userId,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        LocalDateTime createdAt,
        List<OrderItemResponse> items) {
}
