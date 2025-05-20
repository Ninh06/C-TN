package com.example.cdtn.repository;

import com.example.cdtn.entity.products.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE" +
            " (:name IS NULL OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Category> searchByCategoryName(@Param("name") String name, Pageable pageable);
}
