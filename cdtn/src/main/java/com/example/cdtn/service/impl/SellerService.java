package com.example.cdtn.service.impl;

import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {
    @Autowired
    private SellerRepository sellerRepository;

    public Seller getSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người bán với ID: " + sellerId));
    }
}
