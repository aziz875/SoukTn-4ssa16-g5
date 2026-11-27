package com.example.orderservice.controller;

import com.example.orderservice.api.dto.CreateOrderItemRequest;
import com.example.orderservice.api.dto.OrderItemRequest;
import com.example.orderservice.api.dto.OrderItemResponse;
import com.example.orderservice.service.OrderItemService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    private final OrderItemService items;

    public OrderItemController(OrderItemService items) {
        this.items = items;
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> create(@Valid @RequestBody CreateOrderItemRequest request) {
        OrderItemResponse item = items.create(request);
        return ResponseEntity.created(URI.create("/api/order-items/" + item.id())).body(item);
    }

    @GetMapping
    public List<OrderItemResponse> findAll() {
        return items.findAll();
    }

    @GetMapping("/{id}")
    public OrderItemResponse findById(@PathVariable Long id) {
        return items.findById(id);
    }

    @PutMapping("/{id}")
    public OrderItemResponse update(@PathVariable Long id, @Valid @RequestBody OrderItemRequest request) {
        return items.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        items.delete(id);
        return ResponseEntity.noContent().build();
    }
}
