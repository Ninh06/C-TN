package com.example.cdtn.mapper;

import com.example.cdtn.dtos.WishlistDTO;
import com.example.cdtn.dtos.WishlistItemDTO;
import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.repository.BuyerRepository;
import com.example.cdtn.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WishlistMapper {

    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private ProductRepository productRepository;

    public WishlistDTO toWishlistDTO(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        WishlistDTO wishlistDTO = WishlistDTO.builder()
                .wishlistId(wishlist.getWishlistId())
                .createdAt(wishlist.getCreatedAt())
                .build();

        // Set buyerId nếu có
        if (wishlist.getBuyer() != null) {
            wishlistDTO.setBuyerId(wishlist.getBuyer().getBuyerId());
        }

        // Chuyển đổi danh sách sản phẩm sang WishlistItemDTO
        if (wishlist.getProducts() != null && !wishlist.getProducts().isEmpty()) {
            List<WishlistItemDTO> productDTOs = wishlist.getProducts().stream()
                    .map(product -> WishlistItemDTO.builder()
                            .productId(product.getProductId())
                            .productName(product.getName())
                            .currency(product.getCurrency())
                            .minimalVariantPriceAmount(product.getMinimalVariantPriceAmount())
                            .build())
                    .collect(Collectors.toList());
            wishlistDTO.setProducts(productDTOs);
        }

        return wishlistDTO;
    }

    public Wishlist toWishlistEntity(WishlistDTO wishlistDTO) {
        if (wishlistDTO == null) {
            return null;
        }

        Wishlist wishlist = Wishlist.builder()
                .wishlistId(wishlistDTO.getWishlistId())
                .createdAt(wishlistDTO.getCreatedAt() != null ? wishlistDTO.getCreatedAt() : new Date())
                .build();

        // Tìm buyer từ buyerId
        if (wishlistDTO.getBuyerId() != null) {
            buyerRepository.findById(wishlistDTO.getBuyerId()).ifPresent(wishlist::setBuyer);
        }

        // Tìm danh sách Product từ productId
        if (wishlistDTO.getProducts() != null && !wishlistDTO.getProducts().isEmpty()) {
            List<Long> productIds = wishlistDTO.getProducts().stream()
                    .map(WishlistItemDTO::getProductId)
                    .collect(Collectors.toList());

            List<Product> products = productRepository.findAllById(productIds);
            wishlist.setProducts(products);
        }

        return wishlist;
    }


}
