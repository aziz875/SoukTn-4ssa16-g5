package com.example.orderservice.service;

import com.example.orderservice.api.dto.CartItemRequest;
import com.example.orderservice.api.dto.CartResponse;
import com.example.orderservice.api.dto.CreateCartRequest;
import com.example.orderservice.api.dto.UpdateCartRequest;
import java.util.List;

public interface CartService {
    CartResponse create(CreateCartRequest request);

    List<CartResponse> findAll();

    CartResponse findById(Long id);

    CartResponse update(Long id, UpdateCartRequest request);

    CartResponse addItem(Long id, CartItemRequest request);

    CartResponse removeItem(Long id, Long productId);

    void clear(Long id);

    void delete(Long id);
}
