package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.products.CategoryDTO;
import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductTypeWithProductsDTO;
import com.example.cdtn.entity.products.Category;
import com.example.cdtn.entity.products.ProductType;
import com.example.cdtn.mapper.ProductMapper;
import com.example.cdtn.repository.CategoryRepository;
import com.example.cdtn.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private ProductMapper productMapper;

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = ProductMapper.convertToEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);

        return ProductMapper.convertCategoryToDTO(savedCategory);
    }

    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + categoryId));

        return ProductMapper.convertCategoryToDTO(category);
    }

    public Page<CategoryDTO> searchCategoriesByName(String name, Pageable pageable) {
        return categoryRepository.searchByCategoryName(name, pageable)
                .map(ProductMapper::convertCategoryToDTO);
    }

    // Trả về danh sách category chỉ gồm id và name
    public List<CategoryDTO> getAllOnlyCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(ProductMapper::convertOnlyToDTO)
                .collect(Collectors.toList());
    }

    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category không tồn tại với ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public Page<ProductTypeWithProductsDTO> getProductTypesAndProductsByCategory(
            Long categoryId,
            String keyword,
            Pageable pageable) {

        String finalKeyword = (keyword == null) ? "" : keyword;

        List<ProductType> productTypes = productTypeRepository
                .findProductTypesWithProductsByCategoryAndKeyword(categoryId, finalKeyword);

        List<ProductTypeWithProductsDTO> dtoList = productTypes.stream().map(pt -> {
            ProductTypeWithProductsDTO dto = new ProductTypeWithProductsDTO();
            dto.setProductTypeId(pt.getId());
            dto.setProductTypeName(pt.getName());

            List<ProductDTO> filteredProducts = pt.getProducts().stream()
                    .filter(p -> p.getName().toLowerCase().contains(finalKeyword.toLowerCase()))
                    .map(p -> productMapper.toProductDTO(p))
                    .collect(Collectors.toList());

            dto.setProducts(filteredProducts);
            return dto;
        }).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());

        List<ProductTypeWithProductsDTO> pagedList = dtoList.subList(start, end);
        return new PageImpl<>(pagedList, pageable, dtoList.size());
    }


}
