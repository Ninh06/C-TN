package com.example.cdtn.repository;

import com.example.cdtn.entity.users.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByUser_UserId(Long userId);
}
