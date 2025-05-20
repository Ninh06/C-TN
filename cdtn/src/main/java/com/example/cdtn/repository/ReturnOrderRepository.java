package com.example.cdtn.repository;

import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.orders.ReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {
    boolean existsByOrderOrderId(Long orderId);

    ReturnOrder findByOrderOrderId(Long orderId);

    List<ReturnOrder> findByBuyer_BuyerIdOrderByCreatedAtDesc(Long buyerId);

    List<ReturnOrder> findByOrderStatusOrderStatusId(Long statusId);

    boolean existsByOrder(Order order);
}
