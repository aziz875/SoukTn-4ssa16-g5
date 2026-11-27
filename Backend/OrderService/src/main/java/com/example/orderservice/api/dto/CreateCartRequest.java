package com.example.orderservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCartRequest(@NotBlank String userId) {
}
