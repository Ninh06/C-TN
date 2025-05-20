package com.example.cdtn.repository;

import com.example.cdtn.entity.discounts.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByStartDateBeforeAndEndDateAfter(Date currentDate, Date currentDate2);
    Optional<Discount> findByDiscountName(String discountName);
}
