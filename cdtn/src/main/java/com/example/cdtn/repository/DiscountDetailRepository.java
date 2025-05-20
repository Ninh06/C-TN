package com.example.cdtn.repository;

import com.example.cdtn.entity.discounts.DiscountDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountDetailRepository extends JpaRepository<DiscountDetail, Long> {
}
