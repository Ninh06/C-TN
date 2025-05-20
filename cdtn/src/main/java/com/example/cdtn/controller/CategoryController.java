package com.example.cdtn.controller;

import com.example.cdtn.dtos.products.CategoryDTO;
import com.example.cdtn.dtos.products.ProductTypeWithProductsDTO;
import com.example.cdtn.entity.products.Category;
import com.example.cdtn.service.impl.CategoryService;
import com.example.cdtn.service.impl.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể tạo danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        try {
            CategoryDTO categoryDTO = categoryService.getCategoryById(id);
            return ResponseEntity.ok(categoryDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể lấy danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/search")
    public Page<CategoryDTO> searchCategories(@RequestParam(required = false) String name, Pageable pageable) {
        return categoryService.searchCategoriesByName(name, pageable);
    }

    @GetMapping("/all")
    public List<CategoryDTO> getOnlyAllCategories() {
        return categoryService.getAllOnlyCategories();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok("Xóa danh mục thành công với ID: " + id);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể xóa danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{categoryId}/product-types")
    public ResponseEntity<Page<ProductTypeWithProductsDTO>> getProductTypesAndProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductTypeWithProductsDTO> result =
                categoryService.getProductTypesAndProductsByCategory(categoryId, keyword, pageable);

        return ResponseEntity.ok(result);
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
