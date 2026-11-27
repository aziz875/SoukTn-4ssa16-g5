package com.example.orderservice.api.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long orderId,
        Long productId,
        String productName,
        Long shopId,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal) {
}
