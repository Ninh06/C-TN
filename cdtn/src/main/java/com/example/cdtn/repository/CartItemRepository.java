package com.example.cdtn.repository;

import com.example.cdtn.entity.shopcart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
