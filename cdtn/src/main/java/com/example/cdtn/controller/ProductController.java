package com.example.cdtn.controller;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.UpdateProductDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO createdProduct = productService.addProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Lỗi nghiệp vụ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        try {
            ProductDTO productDTO = productService.getProductById(productId);
            return ResponseEntity.ok(productDTO);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/basic")
    public ResponseEntity<List<ProductDTO>> getOnlyProducts() {
        List<ProductDTO> products = productService.getOnlyProducts();
        return ResponseEntity.ok(products);
    }


    @GetMapping("/all")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam(required = false) Long productTypeId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            Pageable pageable) {

        Page<ProductDTO> productDTOs = productService.searchProducts(productTypeId, sellerId, name, minPrice, pageable);
        return ResponseEntity.ok(productDTOs);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok("Xóa sản phẩm thành công!");
        } catch (OurException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id,
                                           @RequestBody @Valid UpdateProductDTO updatedDTO) {
        try {
            ProductDTO result = productService.updateProduct(id, updatedDTO);
            return ResponseEntity.ok(result);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Không tìm thấy: " + e.getMessage());
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
