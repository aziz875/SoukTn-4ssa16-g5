package com.example.orderservice.health;

import com.example.orderservice.repository.OrderRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceHealthIndicator implements HealthIndicator {
    private final OrderRepository orders;

    public OrderServiceHealthIndicator(OrderRepository orders) {
        this.orders = orders;
    }

    @Override
    public Health health() {
        try {
            return Health.up().withDetail("orders", orders.count()).build();
        } catch (DataAccessException exception) {
            return Health.down(exception).build();
        }
    }
}
