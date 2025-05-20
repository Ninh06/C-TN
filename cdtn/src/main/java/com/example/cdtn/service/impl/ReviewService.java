package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.ReviewDTO;
import com.example.cdtn.entity.Review;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.ReviewMapper;
import com.example.cdtn.repository.BuyerRepository;
import com.example.cdtn.repository.OrderRepository;
import com.example.cdtn.repository.ProductRepository;
import com.example.cdtn.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    /** Create*/
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        // Lấy thông tin buyer từ reviewDTO
        Buyer buyer = buyerRepository.findById(reviewDTO.getBuyer().getBuyerId())
                .orElseThrow(() -> new OurException(
                        "Không tìm thấy buyer với ID: " + reviewDTO.getBuyer().getBuyerId()));

        // Lấy thông tin product từ reviewDTO
        Product product = productRepository.findById(reviewDTO.getProduct().getProductId())
                .orElseThrow(() -> new OurException(
                        "Không tìm thấy product với ID: " + reviewDTO.getProduct().getProductId()));

        // Kiểm tra xem buyer đã mua product này chưa, với orderStatus_id = 4 (Đã nhận hàng)
        List<Order> buyerOrders = orderRepository.findByBuyerAndOrderStatus_OrderStatusId(buyer, 4L);

        boolean hasPurchased = buyerOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(orderItem -> orderItem.getProduct().getProductId().equals(product.getProductId()));

        if (!hasPurchased) {
            throw new IllegalStateException("Buyer chưa mua và nhận sản phẩm này nên không thể đánh giá");
        }

        // Kiểm tra xem buyer đã đánh giá product này chưa
        boolean hasReviewed = reviewRepository.existsByBuyerAndProduct(buyer, product);
        if (hasReviewed) {
            throw new IllegalStateException("Buyer đã đánh giá sản phẩm này rồi");
        }

        // Tạo mới review
        Review review = reviewMapper.toReviewEntity(reviewDTO);
        review.setBuyer(buyer);
        review.setProduct(product);
        review.setCreatedAt(new Date());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDTO(savedReview);
    }

    /** Update*/
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy đánh giá với id: " + id));

        // Chỉ update những trường được gửi lên (không ghi đè hết)
        if (reviewDTO.getRating() != null) {
            existingReview.setRating(reviewDTO.getRating());
        }
        if (reviewDTO.getReviewText() != null) {
            existingReview.setReviewText(reviewDTO.getReviewText());
        }
        if (reviewDTO.getProduct() != null) {
            existingReview.setProduct(reviewMapper.toReviewEntity(reviewDTO).getProduct());
        }
        if (reviewDTO.getBuyer() != null) {
            existingReview.setBuyer(reviewMapper.toReviewEntity(reviewDTO).getBuyer());
        }

        Review updatedReview = reviewRepository.save(existingReview);
        return reviewMapper.toReviewDTO(updatedReview);
    }

    /** Get by id*/
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new OurException("Review not found with id: " + id));
        return reviewMapper.toReviewDTO(review);
    }

    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProduct_ProductId(productId);
        return reviews.stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    /** Delete*/
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new OurException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    /** Get all reviews*/
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }
}
