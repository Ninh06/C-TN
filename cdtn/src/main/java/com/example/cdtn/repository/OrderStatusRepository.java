package com.example.cdtn.repository;

import com.example.cdtn.entity.orders.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    /**Tìm trạng thái đơn hàng theo mô tả*/
    List<OrderStatus> findByOrderStatusDescContainingIgnoreCase(String description);

    /**Tìm trạng thái đơn hàng chính xác theo mô tả*/
    Optional<OrderStatus> findByOrderStatusDesc(String description);

    /**Kiểm tra xem một mô tả trạng thái đã tồn tại chưa*/
    boolean existsByOrderStatusDesc(String description);
}
