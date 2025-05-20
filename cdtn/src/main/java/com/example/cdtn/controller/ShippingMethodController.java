package com.example.cdtn.controller;

import com.example.cdtn.dtos.ships.ShippingMethodDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-methods")
@RequiredArgsConstructor
public class ShippingMethodController {
    @Autowired
    private ShippingMethodService shippingMethodService;

    /** Lấy tất cả phương thức giao hàng*/
    @GetMapping
    public ResponseEntity<List<ShippingMethodDTO>> getAllShippingMethods() {
        List<ShippingMethodDTO> methods = shippingMethodService.getAllShippingMethods();
        return ResponseEntity.ok(methods);
    }

    /** Lấy phương thức giao hàng theo ID*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getShippingMethodById(@PathVariable Long id) {
        try {
            ShippingMethodDTO method = shippingMethodService.getShippingMethodById(id);
            return ResponseEntity.ok(method);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Tạo mới phương thức giao hàng*/
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createShippingMethod(@RequestBody ShippingMethodDTO dto) {
        try {
            ShippingMethodDTO created = shippingMethodService.createShippingMethod(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Cập nhật phương thức giao hàng*/
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShippingMethod(@PathVariable Long id,
                                                  @RequestBody ShippingMethodDTO dto) {
        try {
            ShippingMethodDTO updated = shippingMethodService.updateShippingMethod(id, dto);
            return ResponseEntity.ok(updated);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Xoá phương thức giao hàng*/
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteShippingMethod(@PathVariable Long id) {
        try {
            shippingMethodService.deleteShippingMethod(id);
            return ResponseEntity.ok("Xóa thành công!");
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
