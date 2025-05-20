package com.example.cdtn.repository;

import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.entity.users.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByBuyer_BuyerId(Long buyerId);
    Optional<Wishlist> findByBuyer(Buyer buyer);


}
