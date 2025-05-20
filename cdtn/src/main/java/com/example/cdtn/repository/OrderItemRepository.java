package com.example.cdtn.repository;

import com.example.cdtn.entity.orders.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
