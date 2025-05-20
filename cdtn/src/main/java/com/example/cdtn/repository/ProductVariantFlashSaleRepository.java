package com.example.cdtn.repository;

import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.flashsale.ProductVariantFlashSale;
import com.example.cdtn.entity.products.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantFlashSaleRepository extends JpaRepository<ProductVariantFlashSale, Long> {
    void deleteByProductFlashSaleId(Long productFlashSaleId);
    List<ProductVariantFlashSale> findByProductFlashSaleId(Long productFlashSaleId);

    Optional<ProductVariantFlashSale> findByProductFlashSaleAndProductVariant(
            ProductFlashSale productFlashSale,
            ProductVariant productVariant
    );

    ProductVariantFlashSale findByProductVariantId(Long productVariantId);

    List<ProductVariantFlashSale> findByProductVariantIdAndIsActiveTrue(Long productVariantId);

}
