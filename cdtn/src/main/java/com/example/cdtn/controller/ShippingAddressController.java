package com.example.cdtn.controller;

import com.example.cdtn.dtos.ships.ShippingAddressDTO;
import com.example.cdtn.service.impl.ShippingAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyers/{buyerId}/shipping-addresses")
@RequiredArgsConstructor
public class ShippingAddressController {
    @Autowired
    private ShippingAddressService shippingAddressService;

    /** Thêm địa chỉ mới cho buyer*/
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> addShippingAddress(
            @PathVariable Long buyerId,
            @RequestBody ShippingAddressDTO shippingAddressDTO
    ) {
        try {
            ShippingAddressDTO created = shippingAddressService.addShippingAddress(buyerId, shippingAddressDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Lấy danh sách địa chỉ của buyer*/
    @GetMapping
    public ResponseEntity<?> getAllByBuyer(@PathVariable Long buyerId) {
        try {
            List<ShippingAddressDTO> list = shippingAddressService.getShippingAddressesByBuyer(buyerId);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Cập nhật địa chỉ*/
    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> updateShippingAddress(
            @PathVariable Long buyerId,
            @PathVariable Long addressId,
            @RequestBody ShippingAddressDTO shippingAddressDTO
    ) {
        try {
            ShippingAddressDTO updated =
                    shippingAddressService.updateShippingAddress(buyerId, addressId, shippingAddressDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Xoá địa chỉ*/
    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUYER')")
    public ResponseEntity<?> deleteShippingAddress(
            @PathVariable Long buyerId,
            @PathVariable Long addressId
    ) {
        try {
            shippingAddressService.deleteShippingAddress(buyerId, addressId);
            return ResponseEntity.ok("Delete successful!");
        } catch (RuntimeException e) {
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
