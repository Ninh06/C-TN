package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.products.CategoryDTO;
import com.example.cdtn.dtos.products.ProductTypeDTO;
import com.example.cdtn.entity.products.Category;
import com.example.cdtn.entity.products.ProductType;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.ProductMapper;
import com.example.cdtn.mapper.UserMapper;
import com.example.cdtn.repository.CategoryRepository;
import com.example.cdtn.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTypeService {

    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public ProductTypeDTO addProductType(ProductTypeDTO productTypeDTO) {
        if (productTypeDTO == null || productTypeDTO.getCategory() == null) {
            throw new OurException("ProductTypeDTO và Category không được phép null");
        }

        Long categoryId = productTypeDTO.getCategory().getCategoryId();
        if (categoryId == null) {
            throw new OurException("Category ID không được phép null");
        }

        // Lấy category từ DB
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new OurException("Không tìm thấy Category với ID: " + categoryId));

        // Chuyển DTO thành entity
        ProductType productType = new ProductType();
        productType.setName(productTypeDTO.getName());
        productType.setCategory(category);
        productType.setCreatedAt(
                productTypeDTO.getCreatedAt() != null ? productTypeDTO.getCreatedAt() : new Date()
        );

        // Lưu ProductType
        ProductType savedProductType = productTypeRepository.save(productType);

        // Trả lại DTO đã lưu
        return ProductMapper.convertProductTypeToDTO(savedProductType);
    }

    @Transactional(readOnly = true)
    public ProductTypeDTO getProductTypeById(Long productTypeId) {
        ProductType productType = productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new OurException("Không tìm thấy ProductType với ID: " + productTypeId));

        return ProductMapper.convertProductTypeToDTO(productType);
    }

    public Page<ProductTypeDTO> getAllProductTypes(String name, Long categoryId, Pageable pageable) {
        Page<ProductType> productTypePage = productTypeRepository.searchProductTypes(name, categoryId, pageable);

        return productTypePage.map(ProductMapper::convertProductTypeToDTO);
    }

    public List<ProductTypeDTO> getAllProductTypes() {
        List<ProductType> productTypes = productTypeRepository.findAll();
        return productTypes.stream()
                .map(ProductMapper::convertProductTypeToDTOWithoutProducts)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductType(Long id) {
        ProductType productType = productTypeRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy ProductType với ID: " + id));

        productTypeRepository.delete(productType);
    }


}
