package com.example.orderservice.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(Long id, String userId, LocalDateTime updatedAt, List<CartItemResponse> items) {
}
