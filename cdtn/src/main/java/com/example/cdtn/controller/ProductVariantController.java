package com.example.cdtn.controller;

import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;

    /**API thêm mới biến thể sản phẩm*/
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductVariantDTO> addProductVariant(@RequestBody @Valid ProductVariantDTO productVariantDTO)
    {
        try {
            ProductVariantDTO createdProductVariant = productVariantService.addProductVariant(productVariantDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductVariant);
        }  catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (OurException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi hệ thống: " + e.getMessage()
            );
        }
    }

    /** lấy một biến thể sản phẩm theo ID*/
    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantDTO> getProductVariantById(@PathVariable("id") Long id) {
        try {
            ProductVariantDTO productVariantDTO = productVariantService.getProductVariantById(id);
            return ResponseEntity.ok(productVariantDTO);
        } catch (OurException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi hệ thống: " + e.getMessage()
            );
        }
    }

    /** lấy tất cả biến thể của một sản phẩm*/
    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<ProductVariantDTO>> getAllProductVariantsByProductId(
            @PathVariable("productId") Long productId) {
        try {
            List<ProductVariantDTO> productVariantDTOs =
                    productVariantService.getAllProductVariantsByProductId(productId);
            return ResponseEntity.ok(productVariantDTOs);
        } catch (OurException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi hệ thống: " + e.getMessage()
            );
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProductVariantDTO>> searchProductVariants(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductVariantDTO> result = productVariantService.searchProductVariants(productId, name, price, pageable);
        return ResponseEntity.ok(result);
    }


    /** Xóa một biến thể sản phẩm theo ID*/
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<String> deleteProductVariant(@PathVariable("id") Long id) {
        try {
            productVariantService.deleteProductVariant(id);
            return ResponseEntity.ok("Delete successful");
        } catch (OurException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi hệ thống: " + e.getMessage()
            );
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductVariantDTO> updateProductVariant(
            @PathVariable("id") Long id,
            @RequestBody ProductVariantDTO productVariantDTO) {
        try {
            // Gọi phương thức cập nhật từ service
            ProductVariantDTO updated = productVariantService.updateProductVariant(id, productVariantDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Nếu không tìm thấy đối tượng
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Xử lý lỗi khác
        }
    }


}
