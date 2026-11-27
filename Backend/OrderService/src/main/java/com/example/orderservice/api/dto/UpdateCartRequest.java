package com.example.orderservice.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateCartRequest(
        @NotBlank String userId,
        @NotNull List<@Valid CartItemRequest> items) {
}
