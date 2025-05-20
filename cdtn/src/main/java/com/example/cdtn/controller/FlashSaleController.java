package com.example.cdtn.controller;

import com.example.cdtn.dtos.flashsale.AddProductFlashSaleRequest;
import com.example.cdtn.dtos.flashsale.FlashSaleDTO;
import com.example.cdtn.dtos.flashsale.ProductFlashSaleDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.FlashSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/flash-sales")
@RequiredArgsConstructor
public class FlashSaleController {
    @Autowired
    private FlashSaleService flashSaleService;

    /**API để tạo mới một Flash Sale*/
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFlashSale(@RequestBody FlashSaleDTO flashSaleDTO) {
        try {
            System.out.println("Received request: " + flashSaleDTO);
            FlashSaleDTO createdFlashSale = flashSaleService.createFlashSale(flashSaleDTO);
            return new ResponseEntity<>(createdFlashSale, HttpStatus.CREATED);
        } catch (BadRequestException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ErrorResponse("Lỗi hệ thống. Vui lòng thử lại sau."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** API để thêm sản phẩm vào FlashSale hiện có*/
    @PostMapping("/{flashSaleId}/products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> addProductToFlashSale(
            @PathVariable Long flashSaleId,
            @Valid @RequestBody AddProductFlashSaleRequest request) {
        try {
            ProductFlashSaleDTO result = flashSaleService.addProductToFlashSale(
                    flashSaleId,
                    request.getProductId(),
                    request.getQuota(),
                    request.getDiscountPercentage()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Yêu cầu không hợp lệ: " + e.getMessage()));
        } catch (OurException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi hệ thống: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi khi thêm sản phẩm vào FlashSale: " + e.getMessage()));
        }
    }

    @PatchMapping("/products/{productFlashSaleId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> deactivateProductFlashSale(@PathVariable Long productFlashSaleId) {
        try {
            ProductFlashSaleDTO result = flashSaleService.deactivateProductFlashSale(productFlashSaleId);
            return ResponseEntity.ok(result);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Yêu cầu không hợp lệ: " + e.getMessage()));
        } catch (OurException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi hệ thống: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi khi vô hiệu hóa ProductFlashSale: " + e.getMessage()));
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
