package com.example.orderservice.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CreateOrderItemRequest(
        @NotNull @Positive Long orderId,
        @NotNull @Positive Long productId,
        @NotBlank String productName,
        @NotNull @Positive Long shopId,
        @NotNull @PositiveOrZero BigDecimal unitPrice,
        @Positive int quantity) {
}
