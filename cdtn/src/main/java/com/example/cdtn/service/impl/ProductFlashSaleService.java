package com.example.cdtn.service.impl;

import com.example.cdtn.repository.ProductFlashSaleRepository;
import com.example.cdtn.repository.ProductVariantFlashSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFlashSaleService {
    @Autowired
    private ProductFlashSaleRepository productFlashSaleRepository;
    @Autowired
    private ProductVariantFlashSaleRepository productVariantFlashSaleRepository;

    public void deleteProductFlashSaleById(Long id) {
        if (!productFlashSaleRepository.existsById(id)) {
            throw new RuntimeException("ProductFlashSale not found with ID: " + id);
        }
        // Xóa tất cả ProductVariantFlashSale liên quan
        productVariantFlashSaleRepository.deleteByProductFlashSaleId(id);

        // Xóa ProductFlashSale
        productFlashSaleRepository.deleteById(id);
    }
}
