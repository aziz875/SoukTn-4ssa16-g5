package com.example.orderservice.service.impl;

import com.example.orderservice.api.dto.CreateOrderItemRequest;
import com.example.orderservice.api.dto.OrderItemRequest;
import com.example.orderservice.api.dto.OrderItemResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.repository.OrderItemRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderItemService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository items;
    private final OrderRepository orders;

    public OrderItemServiceImpl(OrderItemRepository items, OrderRepository orders) {
        this.items = items;
        this.orders = orders;
    }

    @Override
    public OrderItemResponse create(CreateOrderItemRequest request) {
        Order order = findOrder(request.orderId());
        requirePending(order);
        OrderItem item = order.addItem(
                request.productId(),
                request.productName(),
                request.shopId(),
                request.unitPrice(),
                request.quantity());
        item = items.saveAndFlush(item);
        return toResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> findAll() {
        return items.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponse findById(Long id) {
        return toResponse(findItem(id));
    }

    @Override
    public OrderItemResponse update(Long id, OrderItemRequest request) {
        OrderItem existingItem = findItem(id);
        Order order = findOrder(existingItem.getOrder().getId());
        requirePending(order);
        if (!order.updateItem(
                id,
                request.productId(),
                request.productName(),
                request.shopId(),
                request.unitPrice(),
                request.quantity())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne de commande introuvable");
        }
        orders.save(order);
        return toResponse(findItem(id));
    }

    @Override
    public void delete(Long id) {
        OrderItem item = findItem(id);
        Order order = findOrder(item.getOrder().getId());
        requirePending(order);
        if (!order.removeItem(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne de commande introuvable");
        }
        orders.save(order);
    }

    private Order findOrder(Long id) {
        return orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable"));
    }

    private OrderItem findItem(Long id) {
        return items.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ligne de commande introuvable"));
    }

    private void requirePending(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seules les commandes en attente peuvent être modifiées");
        }
    }

    private OrderItemResponse toResponse(OrderItem item) {
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
