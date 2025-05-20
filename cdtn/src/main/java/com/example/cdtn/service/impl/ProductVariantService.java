package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.ProductMapper;
import com.example.cdtn.repository.ProductRepository;
import com.example.cdtn.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantService {
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    /**Kiểm tra tính hợp lệ của dữ liệu biến thể sản phẩm*/
    private void validateProductVariantData(ProductVariantDTO productVariantDTO) {
        if (productVariantDTO == null) {
            throw new OurException("Dữ liệu biến thể sản phẩm không được để trống");
        }

        if (productVariantDTO.getName() == null || productVariantDTO.getName().trim().isEmpty()) {
            throw new OurException("Tên biến thể sản phẩm không được để trống");
        }

        if (productVariantDTO.getPrice() == null || productVariantDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá biến thể sản phẩm không được âm");
        }

        if (productVariantDTO.getProduct() == null || productVariantDTO.getProduct().getProductId() == null) {
            throw new OurException("Thông tin sản phẩm không được để trống");
        }
    }

    /** Thêm mới biến thể sản phẩm*/
    @Transactional
    public ProductVariantDTO addProductVariant(ProductVariantDTO productVariantDTO) {
        validateProductVariantData(productVariantDTO);

        Product product = productRepository.findById(productVariantDTO.getProduct().getProductId())
                .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm với ID: "
                + productVariantDTO.getProduct().getProductId()));

        ProductVariant productVariant = productMapper.toProductVariantEntity(productVariantDTO);
        // Thiết lập mối quan hệ với sản phẩm
        productVariant.setProduct(product);
        // Đảm bảo ID là null để tạo mới bản ghi
        productVariant.setId(null);
        ProductVariant savedProductVariant = productVariantRepository.save(productVariant);

        return productMapper.toProductVariantDTO(savedProductVariant);
    }

    /**Hiển thị 1 Product Variant theo ID*/
    public ProductVariantDTO getProductVariantById(Long productVariantId) {
        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new OurException("Không tìm thấy biến thể sản phẩm với ID: " + productVariantId));
        return productMapper.toProductVariantDTO(productVariant);
    }

    /**Hiển thị tất cả Product Variant theo Product ID*/
    public List<ProductVariantDTO> getAllProductVariantsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new OurException("Không tìm thấy sản phẩm với ID: " + productId);
        }

        List<ProductVariant> productVariants = productVariantRepository.findByProductProductId(productId);

        return productVariants.stream().map(productMapper::toProductVariantDTO).collect(Collectors.toList());
    }

    public Page<ProductVariantDTO> searchProductVariants(Long productId, String name, Double price, Pageable pageable) {
        Page<ProductVariant> productVariants =
                productVariantRepository.searchProductVariants(productId, name, price, pageable);
        return productVariants.map(productMapper::toProductVariantDTO);
    }


    @Transactional
    public void deleteProductVariant(Long productVariantId) {
        if (!productVariantRepository.existsById(productVariantId)) {
            throw new OurException("Không tìm thấy biến thể sản phẩm với ID: " + productVariantId);
        }
        productVariantRepository.deleteById(productVariantId);
    }

    @Transactional
    public ProductVariantDTO updateProductVariant(Long id, ProductVariantDTO productVariantDTO) {
        // Lấy đối tượng ProductVariant hiện tại từ cơ sở dữ liệu
        ProductVariant existingProductVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy biến thể sản phẩm với ID: " + id));

        // Cập nhật các trường nếu có dữ liệu mới từ productVariantDTO
        if (productVariantDTO.getName() != null && !productVariantDTO.getName().trim().isEmpty()) {
            existingProductVariant.setName(productVariantDTO.getName());
        }

        if (productVariantDTO.getPrice() != null && productVariantDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
            existingProductVariant.setPrice(productVariantDTO.getPrice());
        }

        if (productVariantDTO.getQuantity() != null && productVariantDTO.getQuantity() >= 0) {
            existingProductVariant.setQuantity(productVariantDTO.getQuantity());
        }

        if (productVariantDTO.getWeightVariant() != null && productVariantDTO.getWeightVariant() >= 0) {
            existingProductVariant.setWeightVariant(productVariantDTO.getWeightVariant());
        }

        // Lưu lại đối tượng đã cập nhật vào cơ sở dữ liệu
        ProductVariant updatedProductVariant = productVariantRepository.save(existingProductVariant);

        // Trả về DTO của đối tượng đã cập nhật
        return productMapper.toProductVariantDTO(updatedProductVariant);
    }

    @Transactional(readOnly = true)
    public ProductVariant getVariantById(Long id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy biến thể sản phẩm với ID: " + id));
    }



}
