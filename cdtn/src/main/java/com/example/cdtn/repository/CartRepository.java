package com.example.cdtn.repository;

import com.example.cdtn.entity.shopcart.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<ShoppingCart, Long> {
}
