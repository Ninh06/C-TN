package com.example.cdtn.repository;

import com.example.cdtn.entity.ships.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    Optional<ShippingMethod> findByMethodName(String methodName);
    Optional<ShippingMethod> findByIsDefaultTrue();
}
