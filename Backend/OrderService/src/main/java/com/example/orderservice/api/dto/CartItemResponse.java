package com.example.orderservice.api.dto;

public record CartItemResponse(Long id, Long cartId, Long productId, int quantity) {
}
