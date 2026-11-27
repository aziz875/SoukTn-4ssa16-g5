package com.example.orderservice.service;

import com.example.orderservice.api.dto.CreateOrderItemRequest;
import com.example.orderservice.api.dto.OrderItemRequest;
import com.example.orderservice.api.dto.OrderItemResponse;
import java.util.List;

public interface OrderItemService {
    OrderItemResponse create(CreateOrderItemRequest request);

    List<OrderItemResponse> findAll();

    OrderItemResponse findById(Long id);

    OrderItemResponse update(Long id, OrderItemRequest request);

    void delete(Long id);
}
