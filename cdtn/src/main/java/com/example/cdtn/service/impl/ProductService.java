package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.example.cdtn.dtos.products.UpdateProductDTO;
import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductType;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.ProductMapper;
import com.example.cdtn.mapper.UserMapper;
import com.example.cdtn.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductFlashSaleRepository productFlashSaleRepository;

    /**Kiểm tra tính hợp lệ của dữ liệu sản phẩm*/
    private void validateProductData(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("Dữ liệu sản phẩm không được để trống");
        }

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }

        if (productDTO.getTitle() == null || productDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề sản phẩm không được để trống");
        }

        if (productDTO.getWeight() == null || productDTO.getWeight() <= 0) {
            throw new IllegalArgumentException("Trọng lượng sản phẩm phải lớn hơn 0");
        }

        if (productDTO.getMinimalVariantPriceAmount() == null || productDTO.getMinimalVariantPriceAmount() < 0) {
            throw new IllegalArgumentException("Giá biến thể tối thiểu không được âm");
        }

        if (productDTO.getAvailableForPurchase() == null) {
            throw new IllegalArgumentException("Trạng thái có thể mua không được để trống");
        }

        if (productDTO.getSeller() == null || productDTO.getSeller().getSellerId() == null) {
            throw new IllegalArgumentException("Thông tin người bán không được để trống");
        }

        if (productDTO.getProductType() == null || productDTO.getProductType().getId() == null) {
            throw new IllegalArgumentException("Thông tin loại sản phẩm không được để trống");
        }
    }

    @Transactional
    public ProductDTO addProduct(ProductDTO productDTO) {
        validateProductData(productDTO);

        Seller seller = sellerRepository.findById(productDTO.getSeller().getSellerId())
                .orElseThrow(() -> new OurException("Không tìm thấy Seller id: "
                        + productDTO.getSeller().getSellerId()));

        // Kiểm tra và lấy thông tin loại sản phẩm (ProductType)
        ProductType productType = productTypeRepository.findById(productDTO.getProductType().getId())
                .orElseThrow(() -> new OurException("Không tìm thấy ProductType id: "
                        + productDTO.getProductType().getId()));

        Product product = productMapper.toProductEntity(productDTO);

        product.setSeller(seller);
        product.setProductType(productType);
        product.setProductId(null);

        // Xóa danh sách variants từ đối tượng product trước khi lưu để tránh lưu trùng lặp
        product.setProductVariants(null);

        // Lưu product trước
        Product savedProduct = productRepository.save(product);

        // Tạo và lưu các variants riêng biệt
        List<ProductVariant> variants = new ArrayList<>();

        if (productDTO.getProductVariants() != null && !productDTO.getProductVariants().isEmpty()) {
            variants = productDTO.getProductVariants().stream()
                    .map(variantDTO -> {
                        ProductVariant variant = productMapper.toProductVariantEntity(variantDTO);
                        variant.setProduct(savedProduct);
                        return variant;
                    }).collect(Collectors.toList());

            // Lưu các variants sau khi đã tạo product
            productVariantRepository.saveAll(variants);
        }

        // Tạo DTO kết quả bằng cách ánh xạ từ entity đã lưu
        ProductDTO resultDTO = productMapper.toProductDTO(savedProduct);

        // Thêm variants vào resultDTO sau khi chúng đã được lưu
        resultDTO.setProductVariants(
                variants.stream().map(productMapper::toProductVariantDTO).collect(Collectors.toList())
        );

        return resultDTO;
    }

    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm với ID: " + productId));

        // Map entity -> DTO
        ProductDTO productDTO = productMapper.toProductDTO(product);

        // Lấy danh sách các biến thể nếu có
        List<ProductVariant> productVariants = productVariantRepository.findByProductProductId(productId);
        productDTO.setProductVariants(
                productVariants.stream()
                        .map(productMapper::toProductVariantDTO)
                        .collect(Collectors.toList())
        );

        return productDTO;
    }

    @Transactional(readOnly = true)
    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm với ID: " + id));
    }


    public List<ProductDTO> getOnlyProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toOnLyProductDTO)
                .collect(Collectors.toList());
    }



    public Page<ProductDTO> searchProducts(Long productTypeId,
                                           Long sellerId,
                                           String name,
                                           Double minPrice,
                                           Pageable pageable) {
        // Nếu người dùng bật tìm kiếm theo name nhưng không nhập gì thì trả về danh sách rỗng
        if (name != null && name.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Product> productPage = productRepository.searchProductsByCriteria(
                productTypeId, sellerId, name, minPrice, pageable);

        return productPage.map(product -> {
            ProductDTO productDTO = productMapper.toProductDTO(product);
            List<ProductVariant> productVariants = productVariantRepository.findByProductProductId(product.getProductId());
            productDTO.setProductVariants(
                    productVariants.stream()
                            .map(productMapper::toProductVariantDTO)
                            .collect(Collectors.toList())
            );
            return productDTO;
        });
    }


    @Transactional
    public void deleteProduct(Long productId) {
        // Kiểm tra xem sản phẩm có tồn tại hay không
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm với ID: " + productId));

        // Xóa sản phẩm, các biến thể sẽ bị xóa tự động nhờ cascade
        productRepository.delete(product);
    }

    @Transactional
    public ProductDTO updateProduct(Long productId, UpdateProductDTO updatedDTO) {
        // Tìm sản phẩm cần cập nhật
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm với ID: " + productId));

        // Cập nhật các trường được phép
        if (updatedDTO.getName() != null) {
            existingProduct.setName(updatedDTO.getName());
        }

        if (updatedDTO.getTitle() != null) {
            existingProduct.setTitle(updatedDTO.getTitle());
        }

        if (updatedDTO.getDescription() != null) {
            existingProduct.setDescription(updatedDTO.getDescription());
        }

        if (updatedDTO.getWeight() != null) {
            existingProduct.setWeight(updatedDTO.getWeight());
        }

        if (updatedDTO.getCurrency() != null) {
            existingProduct.setCurrency(updatedDTO.getCurrency());
        }

        if (updatedDTO.getMinimalVariantPriceAmount() != null) {
            existingProduct.setMinimalVariantPriceAmount(updatedDTO.getMinimalVariantPriceAmount());
        }

        if (updatedDTO.getAvailableForPurchase() != null) {
            existingProduct.setAvailableForPurchase(updatedDTO.getAvailableForPurchase());
        }

        if (updatedDTO.getMinimalQuantity() != null) {
            existingProduct.setMinimalQuantity(updatedDTO.getMinimalQuantity());
        }

        // Lưu lại thay đổi
        Product savedProduct = productRepository.save(existingProduct);

        // Trả về DTO
        return productMapper.toProductDTO(savedProduct);
    }

    /**Lấy thông tin sản phẩm kèm giá Flash Sale nếu có*/
    public ProductDTO getProductWithFlashSalePrice(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm với ID: " + productId));

        ProductDTO productDTO = productMapper.toProductDTO(product);

        // Kiểm tra xem sản phẩm có đang tham gia flash sale nào không
        Date currentTime = new Date();
        Optional<ProductFlashSale> activeFlashSale = productFlashSaleRepository
                .findActiveFlashSalesByProductId(productId, currentTime)
                .stream()
                .filter(pfs -> pfs.getFlashSale().getIsActive())
                .findFirst();

        if (activeFlashSale.isPresent()) {
            ProductFlashSale flashSaleProduct = activeFlashSale.get();

            // Kiểm tra xem flash sale còn quota không
            if (flashSaleProduct.getSoldCount() < flashSaleProduct.getQuota()) {
                // Cập nhật giá cho product
                productDTO.setOriginalPrice(BigDecimal.valueOf(flashSaleProduct.getOriginalPrice()));
                productDTO.setFlashSalePrice(BigDecimal.valueOf(flashSaleProduct.getFlashSalePrice()));
                productDTO.setDiscountPercentage(flashSaleProduct.getDiscountPercentage());
                productDTO.setIsInFlashSale(true);
                productDTO.setFlashSaleQuota(flashSaleProduct.getQuota());
                productDTO.setFlashSaleSoldCount(flashSaleProduct.getSoldCount());

                // Tính phần trăm giảm giá
                Double discountPercentage = 1.0 - (flashSaleProduct.getFlashSalePrice() / flashSaleProduct.getOriginalPrice());

                // Cập nhật giá cho mỗi biến thể sản phẩm
                if (productDTO.getProductVariants() != null && !productDTO.getProductVariants().isEmpty()) {
                    for (ProductVariantDTO variantDTO : productDTO.getProductVariants()) {
                        // Lưu giá gốc
                        BigDecimal originalPrice = variantDTO.getPrice();
                        variantDTO.setOriginalPrice(originalPrice);

                        // Tính giá flash sale cho biến thể theo cùng tỉ lệ giảm giá
                        BigDecimal flashSalePrice = originalPrice.multiply(BigDecimal.valueOf(1.0 - discountPercentage));
                        variantDTO.setFlashSalePrice(flashSalePrice);
                        variantDTO.setPrice(flashSalePrice); // Cập nhật giá hiển thị
                        variantDTO.setDiscountPercentage(flashSaleProduct.getDiscountPercentage());
                        variantDTO.setIsInFlashSale(true);
                    }
                }

                // Cập nhật giá tối thiểu của sản phẩm
                productDTO.setMinimalVariantPriceAmount(calculateMinimalFlashSalePrice(product, discountPercentage));
            }
        }

        return productDTO;
    }

    /**Tính giá tối thiểu của sản phẩm trong Flash Sale*/
    private Double calculateMinimalFlashSalePrice(Product product, Double discountPercentage) {
        if (product.getProductVariants() == null || product.getProductVariants().isEmpty()) {
            return product.getMinimalVariantPriceAmount() * (1.0 - discountPercentage);
        }

        Double minPrice = product.getProductVariants().stream()
                .map(variant -> variant.getPrice().doubleValue() * (1.0 - discountPercentage))
                .min(Double::compare)
                .orElse(product.getMinimalVariantPriceAmount());

        return minPrice;
    }

    /** Lấy danh sách các sản phẩm đang tham gia Flash Sale*/
    public List<ProductDTO> getFlashSaleProducts() {
        Date currentTime = new Date();
        List<Product> flashSaleProducts = productFlashSaleRepository.findProductsInActiveFlashSale(currentTime);

        return flashSaleProducts.stream()
                .map(product -> getProductWithFlashSalePrice(product.getProductId()))
                .collect(Collectors.toList());
    }


}
