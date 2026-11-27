package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Override
    @EntityGraph(attributePaths = "order")
    List<OrderItem> findAll();

    @Override
    @EntityGraph(attributePaths = "order")
    Optional<OrderItem> findById(Long id);
}
