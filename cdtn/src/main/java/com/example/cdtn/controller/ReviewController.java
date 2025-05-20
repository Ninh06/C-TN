package com.example.cdtn.controller;

import com.example.cdtn.dtos.ReviewDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    /** Create review*/
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (OurException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorResponse("Lỗi server: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** Update review*/
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
            return ResponseEntity.ok(updatedReview);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Get review by id*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        try {
            ReviewDTO reviewDTO = reviewService.getReviewById(id);
            return ResponseEntity.ok(reviewDTO);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewDTO> reviewDTOs = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviewDTOs);
    }

    /** Get all reviews*/
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    /** Delete review*/
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Delete successful!");
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
