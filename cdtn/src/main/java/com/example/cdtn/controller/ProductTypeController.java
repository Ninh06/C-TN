package com.example.cdtn.controller;

import com.example.cdtn.dtos.products.ProductTypeDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.ProductTypeService;
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

import java.util.List;

@RestController
@RequestMapping("/api/product-types")
public class ProductTypeController {

    @Autowired
    private ProductTypeService productTypeService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProductType(@Valid @RequestBody ProductTypeDTO productTypeDTO) {
        try {
            ProductTypeDTO savedProductType = productTypeService.addProductType(productTypeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProductType);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Dữ liệu không hợp lệ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getProductTypeById(@PathVariable("id") Long id) {
        try {
            ProductTypeDTO result = productTypeService.getProductTypeById(id);
            return ResponseEntity.ok(result);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Không tìm thấy loại sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping
    public ResponseEntity<Page<ProductTypeDTO>> getAllProductTypes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductTypeDTO> productTypes = productTypeService.getAllProductTypes(name, categoryId, pageable);
        return ResponseEntity.ok(productTypes);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductTypeDTO>> getAllProductTypes() {
        List<ProductTypeDTO> result = productTypeService.getAllProductTypes();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProductType(@PathVariable Long id) {
        try {
            productTypeService.deleteProductType(id);
            return ResponseEntity.ok("Delete successful!");
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Không thể xóa loại sản phẩm: " + e.getMessage());
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
