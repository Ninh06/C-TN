package com.example.cdtn.repository;

import com.example.cdtn.entity.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
