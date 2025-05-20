package com.example.cdtn.repository;

import com.example.cdtn.entity.products.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductProductId(Long productId);

    @Query("SELECT pv FROM ProductVariant pv WHERE " +
            "(:productId IS NULL OR :productId = 0 OR pv.product.productId = :productId) " +
            "AND (:name IS NULL OR :name = '' OR pv.name LIKE %:name%) " +
            "AND (:price IS NULL OR pv.price = :price)")
    Page<ProductVariant> searchProductVariants(@Param("productId") Long productId,
                                               @Param("name") String name,
                                               @Param("price") Double price,
                                               Pageable pageable);

}
