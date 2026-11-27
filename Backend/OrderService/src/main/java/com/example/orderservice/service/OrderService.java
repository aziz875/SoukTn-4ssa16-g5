package com.example.orderservice.service;

import com.example.orderservice.api.dto.CreateOrderRequest;
import com.example.orderservice.api.dto.OrderResponse;
import com.example.orderservice.api.dto.UpdateOrderRequest;
import com.example.orderservice.entity.OrderStatus;
import java.util.List;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request);

    List<OrderResponse> findAll();

    OrderResponse findById(Long id);

    OrderResponse update(Long id, UpdateOrderRequest request);

    OrderResponse changeStatus(Long id, OrderStatus nextStatus);

    void delete(Long id);
}
