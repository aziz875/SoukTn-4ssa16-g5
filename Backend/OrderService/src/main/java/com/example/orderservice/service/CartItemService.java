package com.example.orderservice.service;

import com.example.orderservice.api.dto.CartItemResponse;
import com.example.orderservice.api.dto.CreateCartItemRequest;
import com.example.orderservice.api.dto.UpdateCartItemRequest;
import java.util.List;

public interface CartItemService {
    CartItemResponse create(CreateCartItemRequest request);

    List<CartItemResponse> findAll();

    CartItemResponse findById(Long id);

    CartItemResponse update(Long id, UpdateCartItemRequest request);

    void delete(Long id);
}
