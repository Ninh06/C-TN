package com.example.cdtn.repository;

import com.example.cdtn.entity.ships.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingZoneRepository extends JpaRepository<ShippingZone, Long> {
    Optional<ShippingZone> findByZoneName(String zoneName);
}
