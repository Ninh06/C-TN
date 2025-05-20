package com.example.cdtn.repository;

import com.example.cdtn.entity.ships.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByBuyerBuyerId(Long buyerId);
}
