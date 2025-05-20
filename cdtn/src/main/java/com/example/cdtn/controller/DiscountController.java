package com.example.cdtn.controller;

import com.example.cdtn.dtos.discounts.DiscountDTO;
import com.example.cdtn.service.impl.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
    @Autowired
    private DiscountService discountService;


    /**Tạo mới một chương trình giảm giá*/
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDiscount(@RequestBody DiscountDTO discountDTO) {
        try {
            DiscountDTO createdDiscount = discountService.createDiscount(discountDTO);
            return new ResponseEntity<>(createdDiscount, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể tạo chương trình giảm giá: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**Lấy thông tin một chương trình giảm giá theo ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDiscountById(@PathVariable("id") Long id) {
        try {
            DiscountDTO discountDTO = discountService.getDiscountById(id);
            return ResponseEntity.ok(discountDTO);
        } catch (RuntimeException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi không xác định.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**Lấy danh sách tất cả các chương trình giảm giá*/
    @GetMapping
    public ResponseEntity<List<DiscountDTO>> getAllDiscounts() {
        List<DiscountDTO> discounts = discountService.getAllDiscounts();
        return ResponseEntity.ok(discounts);
    }

    /**Cập nhật thông tin một chương trình giảm giá*/
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDiscount(@PathVariable("id") Long id,
                                            @RequestBody DiscountDTO discountDTO) {
        try {
            DiscountDTO updatedDiscount = discountService.updateDiscount(id, discountDTO);
            return ResponseEntity.ok(updatedDiscount);
        } catch (RuntimeException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi trong quá trình cập nhật.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    /**Xóa một chương trình giảm giá*/
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDiscount(@PathVariable("id") Long id) {
        try {
            discountService.deleteDiscount(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (RuntimeException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi trong quá trình xóa.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**Lấy danh sách các chương trình giảm giá đang hoạt động*/
    @GetMapping("/active")
    public ResponseEntity<List<DiscountDTO>> getActiveDiscounts() {
        List<DiscountDTO> activeDiscounts = discountService.getActiveDiscounts();
        return ResponseEntity.ok(activeDiscounts);
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
