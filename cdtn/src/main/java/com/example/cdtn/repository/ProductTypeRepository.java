package com.example.cdtn.repository;

import com.example.cdtn.entity.products.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    @Query("SELECT pt FROM ProductType pt WHERE " +
            "(:name IS NULL OR LOWER(pt.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryId IS NULL OR pt.category.categoryId = :categoryId)")
    Page<ProductType> searchProductTypes(@Param("name") String name,
                                         @Param("categoryId") Long categoryId,
                                         Pageable pageable);

    // ProductTypeRepository.java
    @Query("SELECT DISTINCT pt FROM ProductType pt " +
            "LEFT JOIN FETCH pt.products p " +
            "WHERE pt.category.categoryId = :categoryId " +
            "AND (LOWER(pt.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ProductType> findProductTypesWithProductsByCategoryAndKeyword(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword);

    List<ProductType> findByCategoryCategoryId(Long categoryId);


}
