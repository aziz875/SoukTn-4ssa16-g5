package com.example.orderservice.api.dto;

import com.example.orderservice.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeOrderStatusRequest(@NotNull OrderStatus status) {
}
