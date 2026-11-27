package com.example.orderservice.controller;

import com.example.orderservice.api.dto.CartItemResponse;
import com.example.orderservice.api.dto.CreateCartItemRequest;
import com.example.orderservice.api.dto.UpdateCartItemRequest;
import com.example.orderservice.service.CartItemService;
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
@RequestMapping("/api/cart-items")
public class CartItemController {
    private final CartItemService items;

    public CartItemController(CartItemService items) {
        this.items = items;
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> create(@Valid @RequestBody CreateCartItemRequest request) {
        CartItemResponse item = items.create(request);
        return ResponseEntity.created(URI.create("/api/cart-items/" + item.id())).body(item);
    }

    @GetMapping
    public List<CartItemResponse> findAll() {
        return items.findAll();
    }

    @GetMapping("/{id}")
    public CartItemResponse findById(@PathVariable Long id) {
        return items.findById(id);
    }

    @PutMapping("/{id}")
    public CartItemResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCartItemRequest request) {
        return items.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        items.delete(id);
        return ResponseEntity.noContent().build();
    }
}
