package com.example.cdtn.repository;

import com.example.cdtn.entity.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE " +
            "(:productTypeId IS NULL OR p.productType.id = :productTypeId) " +
            "AND (:sellerId IS NULL OR p.seller.id = :sellerId) " +
            "AND (:name IS NULL OR p.name LIKE %:name%) " +
            "AND (:minPrice IS NULL OR p.minimalVariantPriceAmount >= :minPrice)")
    Page<Product> searchProductsByCriteria(@Param("productTypeId") Long productTypeId,
                                           @Param("sellerId") Long sellerId,
                                           @Param("name") String name,
                                           @Param("minPrice") Double minPrice,
                                           Pageable pageable);


}
