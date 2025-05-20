package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.WishlistDTO;
import com.example.cdtn.dtos.WishlistItemDTO;
import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.UserMapper;
import com.example.cdtn.repository.BuyerRepository;
import com.example.cdtn.repository.ProductRepository;
import com.example.cdtn.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private ProductRepository productRepository;

    /**Hiển thị thông tin chi tiết Wishlist*/
    public WishlistDTO getWishlistById(Long wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new OurException("Không tìm thấy wishlist với ID: " + wishlistId));

        return this.toWishlistDTO(wishlist);
    }


    /**Thêm product vào Wishlist*/
    @Transactional
    public WishlistDTO addProductToWishlist(Long buyerId, Long productId) {

        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người mua"));

        Wishlist wishlist = wishlistRepository.findByBuyer(buyer)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setBuyer(buyer);
                    newWishlist.setCreatedAt(new Date());
                    return newWishlist;
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (wishlist.getProducts() == null) {
            wishlist.setProducts(new ArrayList<>());
        }

        if (!wishlist.getProducts().contains(product)) {
            wishlist.getProducts().add(product);
        }

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return toWishlistDTO(savedWishlist);
    }

    /**Xóa product ở Wishlist*/
    @Transactional
    public void removeProductFromWishlist(Long buyerId, Long productId) {
        // Lấy Buyer
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người mua"));

        // Lấy Wishlist của Buyer
        Wishlist wishlist = wishlistRepository.findByBuyer(buyer)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh sách mong muốn cho người mua này"));

        // Lấy Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Xóa sản phẩm khỏi Wishlist nếu có
        if (wishlist.getProducts() != null && wishlist.getProducts().contains(product)) {
            wishlist.getProducts().remove(product);
        } else {
            throw new OurException("Sản phẩm không tồn tại trong danh sách mong muốn của người mua");
        }

        wishlistRepository.save(wishlist);  

      
    }


    private WishlistDTO toWishlistDTO(Wishlist wishlist) {
        return WishlistDTO.builder()
                .wishlistId(wishlist.getWishlistId())
                .buyerId(wishlist.getBuyer().getBuyerId())
                .createdAt(wishlist.getCreatedAt())
                .products(
                        wishlist.getProducts() != null
                                ? wishlist.getProducts().stream()
                                .map(product -> WishlistItemDTO.builder()
                                        .productId(product.getProductId())
                                        .productName(product.getName())
                                        .minimalVariantPriceAmount(product.getMinimalVariantPriceAmount())
                                        .currency(product.getCurrency())
                                        .build())
                                .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .build();
    }
}
