package org.com.orderservice.repository;

import org.com.orderservice.model.Order;
import org.com.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);

    // Найти заказы по статусу
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findByIdAndUserId(UUID id, UUID userId);
}
