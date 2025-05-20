package com.example.cdtn.mapper;

import com.example.cdtn.dtos.ReviewDTO;
import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.example.cdtn.entity.Review;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.users.Buyer;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public ReviewDTO toReviewDTO(Review review) {
        if(review == null) {
            return null;
        }

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .build();

        if(review.getProduct() != null) {
            ProductDTO productDTO = ProductDTO.builder()
                    .productId(review.getProduct().getProductId())
                    .name(review.getProduct().getName())
                    .title(review.getProduct().getTitle())
                    .description(review.getProduct().getDescription())
                    .weight(review.getProduct().getWeight())
                    .currency(review.getProduct().getCurrency())
                    .minimalVariantPriceAmount(review.getProduct().getMinimalVariantPriceAmount())
                    .minimalQuantity(review.getProduct().getMinimalQuantity())
                    .availableForPurchase(review.getProduct().getAvailableForPurchase())
                    .build();
            reviewDTO.setProduct(productDTO);
        }

        if (review.getBuyer() != null) {
            BuyerDTO buyerDTO = BuyerDTO.builder()
                    .buyerId(review.getBuyer().getBuyerId())
                    .build();
            reviewDTO.setBuyer(buyerDTO);
        }
        return reviewDTO;
    }

    public Review toReviewEntity(ReviewDTO reviewDTO) {
        if (reviewDTO == null) {
            return null;
        }

        Review review = Review.builder()
                .reviewId(reviewDTO.getReviewId())
                .rating(reviewDTO.getRating())
                .reviewText(reviewDTO.getReviewText())
                .build();

        if (reviewDTO.getProduct() != null) {
            Product product = Product.builder()
                    .productId(reviewDTO.getProduct().getProductId())
                    .name(reviewDTO.getProduct().getName())
                    .title(reviewDTO.getProduct().getTitle())
                    .description(reviewDTO.getProduct().getDescription())
                    .weight(reviewDTO.getProduct().getWeight())
                    .currency(reviewDTO.getProduct().getCurrency())
                    .minimalVariantPriceAmount(reviewDTO.getProduct().getMinimalVariantPriceAmount())
                    .minimalQuantity(reviewDTO.getProduct().getMinimalQuantity())
                    .availableForPurchase(reviewDTO.getProduct().getAvailableForPurchase())
                    .build();
            review.setProduct(product);
        }

        if (reviewDTO.getBuyer() != null) {
            Buyer buyer = Buyer.builder()
                    .buyerId(reviewDTO.getBuyer().getBuyerId())
                    .build();
            review.setBuyer(buyer);
        }

        return review;
    }



}
