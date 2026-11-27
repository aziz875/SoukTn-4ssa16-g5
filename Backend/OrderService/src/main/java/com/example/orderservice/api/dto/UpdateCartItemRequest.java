package com.example.orderservice.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(
        @NotNull @Positive Long productId,
        @Positive int quantity) {
}
