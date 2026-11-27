package com.example.orderservice.service.impl;

import com.example.orderservice.api.dto.CartItemResponse;
import com.example.orderservice.api.dto.CreateCartItemRequest;
import com.example.orderservice.api.dto.UpdateCartItemRequest;
import com.example.orderservice.entity.Cart;
import com.example.orderservice.entity.CartItem;
import com.example.orderservice.repository.CartItemRepository;
import com.example.orderservice.repository.CartRepository;
import com.example.orderservice.service.CartItemService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository items;
    private final CartRepository carts;

    public CartItemServiceImpl(CartItemRepository items, CartRepository carts) {
        this.items = items;
        this.carts = carts;
    }

    @Override
    public CartItemResponse create(CreateCartItemRequest request) {
        Cart cart = findCart(request.cartId());
        CartItem item = cart.addItem(request.productId(), request.quantity());
        item = items.saveAndFlush(item);
        return toResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> findAll() {
        return items.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CartItemResponse findById(Long id) {
        return toResponse(findItem(id));
    }

    @Override
    public CartItemResponse update(Long id, UpdateCartItemRequest request) {
        CartItem existingItem = findItem(id);
        Cart cart = findCart(existingItem.getCart().getId());
        CartItem item;
        try {
            item = cart.updateItem(id, request.productId(), request.quantity());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article du panier introuvable");
        }
        carts.save(cart);
        return toResponse(item);
    }

    @Override
    public void delete(Long id) {
        CartItem item = findItem(id);
        Cart cart = findCart(item.getCart().getId());
        if (!cart.removeItemById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article du panier introuvable");
        }
        carts.save(cart);
    }

    private Cart findCart(Long id) {
        return carts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Panier introuvable"));
    }

    private CartItem findItem(Long id) {
        return items.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article du panier introuvable"));
    }

    private CartItemResponse toResponse(CartItem item) {
        return new CartItemResponse(item.getId(), item.getCart().getId(), item.getProductId(), item.getQuantity());
    }
}
