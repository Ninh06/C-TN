package com.example.cdtn.controller;

import com.example.cdtn.dtos.WishlistDTO;
import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/{wishlistId}")
    public ResponseEntity<?> getWishlistById(@PathVariable Long wishlistId) {
        try {
            WishlistDTO wishlistDTO = wishlistService.getWishlistById(wishlistId);
            return ResponseEntity.ok(wishlistDTO);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Lỗi khi lấy wishlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @PostMapping("/add/{buyerId}/{productId}")
    public ResponseEntity<?> addProductToWishlist(
            @PathVariable("buyerId") Long buyerId,
            @PathVariable("productId") Long productId) {
        try {
            WishlistDTO wishlistDTO = wishlistService.addProductToWishlist(buyerId, productId);
            return ResponseEntity.ok(wishlistDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse("Lỗi khi thêm sản phẩm vào wishlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @DeleteMapping("/{buyerId}/products/{productId}")
    public ResponseEntity<?> removeProductFromWishlist(
            @PathVariable("buyerId") Long buyerId,
            @PathVariable("productId") Long productId) {

        try {
            wishlistService.removeProductFromWishlist(buyerId, productId);
            return ResponseEntity.ok("Xóa sản phẩm khỏi danh sách mong muốn thành công!");
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Đã xảy ra lỗi: " + e.getMessage());
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
