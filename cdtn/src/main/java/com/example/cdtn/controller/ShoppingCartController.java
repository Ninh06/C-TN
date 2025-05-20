package com.example.cdtn.controller;

import com.example.cdtn.dtos.shopcart.ShoppingCartDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> addProductToCart(
            @RequestParam Long buyerId,
            @RequestParam Long productId,
            @RequestParam(required = false) Long productVariantId,
            @RequestParam Integer quantity) {
        try {
            ShoppingCartDTO updatedCart =
                    shoppingCartService.addProductToCart(buyerId, productId, productVariantId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> removeProductFromCart(
            @RequestParam Long buyerId,
            @RequestParam Long cartItemId) {
        try {
            ShoppingCartDTO updatedCart = shoppingCartService.removeProductFromCart(buyerId, cartItemId);
            return ResponseEntity.ok(updatedCart);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @PutMapping("/update-quantity")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> updateCartItemQuantity(
            @RequestParam Long buyerId,
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity) {
        try {
            ShoppingCartDTO updatedCart = shoppingCartService.updateCartItemQuantity(buyerId, cartItemId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/{buyerId}")
    public ResponseEntity<?> getShoppingCart(@PathVariable Long buyerId) {
        try {
            ShoppingCartDTO shoppingCartDTO = shoppingCartService.getShoppingCartByBuyerId(buyerId);
            return ResponseEntity.ok(shoppingCartDTO);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @DeleteMapping("/clear/{buyerId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> clearShoppingCart(@PathVariable Long buyerId) {
        try {
            ShoppingCartDTO emptyCart = shoppingCartService.clearShoppingCart(buyerId);
            return ResponseEntity.ok(emptyCart);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
