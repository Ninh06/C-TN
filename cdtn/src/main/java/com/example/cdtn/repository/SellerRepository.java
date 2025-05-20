package com.example.cdtn.repository;

import com.example.cdtn.entity.users.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
