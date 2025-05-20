package com.example.cdtn.repository;

import com.example.cdtn.entity.Review;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.users.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_ProductId(Long productId);

    boolean existsByBuyerAndProduct(Buyer buyer, Product product);
}
