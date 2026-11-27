package com.example.orderservice.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank String orderNumber,
        @NotBlank String userId,
        @NotBlank String shippingAddress,
        @NotEmpty List<@Valid OrderItemRequest> items) {
}
