package com.example.orderservice.service.impl;

import com.example.orderservice.api.dto.CreateOrderRequest;
import com.example.orderservice.api.dto.OrderItemRequest;
import com.example.orderservice.api.dto.OrderItemResponse;
import com.example.orderservice.api.dto.OrderResponse;
import com.example.orderservice.api.dto.UpdateOrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orders;

    public OrderServiceImpl(OrderRepository orders) {
        this.orders = orders;
    }

    @Override
    public OrderResponse create(CreateOrderRequest request) {
        if (orders.existsByOrderNumber(request.orderNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce numéro de commande existe déjà");
        }
        Order order = new Order(request.orderNumber(), request.userId(), request.shippingAddress());
        request.items().forEach(item -> addItem(order, item));
        return toResponse(orders.save(order));
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findAll() {
        return orders.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse findById(Long id) {
        return toResponse(findOrder(id));
    }

    @Override
    public OrderResponse update(Long id, UpdateOrderRequest request) {
        Order order = findOrder(id);
        requirePending(order);
        order.setUserId(request.userId());
        order.setShippingAddress(request.shippingAddress());
        order.clearItems();
        request.items().forEach(item -> addItem(order, item));
        return toResponse(order);
    }

    @Override
    public OrderResponse changeStatus(Long id, OrderStatus nextStatus) {
        Order order = findOrder(id);
        try {
            order.changeStatus(nextStatus);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
        return toResponse(order);
    }

    @Override
    public void delete(Long id) {
        Order order = findOrder(id);
        requirePending(order);
        orders.delete(order);
    }

    private void requirePending(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Seules les commandes en attente peuvent être modifiées ou supprimées");
        }
    }

    private Order findOrder(Long id) {
        return orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable"));
    }

    private void addItem(Order order, OrderItemRequest item) {
        order.addItem(item.productId(), item.productName(), item.shopId(), item.unitPrice(), item.quantity());
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                items);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getOrder().getId(),
                item.getProductId(),
                item.getProductName(),
                item.getShopId(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.lineTotal());
    }
}
