package com.example.orderservice.repository;

import com.example.orderservice.entity.Cart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Override
    @EntityGraph(attributePaths = "items")
    List<Cart> findAll();

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<Cart> findById(Long id);
}
