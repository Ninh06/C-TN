package com.example.cdtn.mapper;

import com.example.cdtn.dtos.ReviewDTO;
import com.example.cdtn.dtos.WishlistDTO;
import com.example.cdtn.dtos.flashsale.ProductFlashSaleDTO;
import com.example.cdtn.dtos.orders.OrderItemDTO;
import com.example.cdtn.dtos.orders.ReturnOrderDTO;
import com.example.cdtn.dtos.products.CategoryDTO;
import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductTypeDTO;
import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.example.cdtn.dtos.shopcart.CartItemDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.example.cdtn.entity.Review;
import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.orders.OrderItem;
import com.example.cdtn.entity.orders.ReturnOrder;
import com.example.cdtn.entity.products.Category;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductType;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.shopcart.CartItem;
import com.example.cdtn.entity.users.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    // Phương thức chuyển đổi từ Category sang CategoryDTO
    public static CategoryDTO convertCategoryToDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
//                .createdAt(category.getCreatedAt())
                .build();

        // Chuyển đổi danh sách ProductType sang ProductTypeDTO nếu có
        if (category.getProductTypes() != null && !category.getProductTypes().isEmpty()) {
            List<ProductTypeDTO> productTypeDTOList = category.getProductTypes().stream()
                    .map(productType -> ProductTypeDTO.builder()
                            .id(productType.getId())
                            .name(productType.getName())
                            .createdAt(productType.getCreatedAt())
                            // Không set category ở đây để tránh vòng lặp vô hạn
                            .build())
                    .toList();

            categoryDTO.setProductTypes(productTypeDTOList);
        }

        return categoryDTO;
    }

    public static CategoryDTO convertOnlyToDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();


        return categoryDTO;
    }

    // Phương thức chuyển đổi từ CategoryDTO sang Category
    public static Category convertToEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }

        Category category = Category.builder()
                .categoryId(categoryDTO.getCategoryId())
                .categoryName(categoryDTO.getCategoryName())
//                .createdAt(categoryDTO.getCreatedAt())
                .build();

        // Chuyển đổi từ ProductTypeDTO sang ProductType nếu có
        if (categoryDTO.getProductTypes() != null && !categoryDTO.getProductTypes().isEmpty()) {
            List<ProductType> productTypeList = categoryDTO.getProductTypes().stream()
                    .map(productTypeDTO -> {
                        ProductType productType = ProductType.builder()
                                .id(productTypeDTO.getId())
                                .name(productTypeDTO.getName())
//                                .createdAt(productTypeDTO.getCreatedAt())
                                .build();
                        // Thiết lập quan hệ ngược lại
                        productType.setCategory(category);
                        return productType;
                    })
                    .toList();

            category.setProductTypes(productTypeList);
        }

        return category;
    }

    // Phương thức chuyển đổi từ ProductType sang ProductTypeDTO
    public static ProductTypeDTO convertProductTypeToDTO(ProductType productType) {
        if (productType == null) {
            return null;
        }

        ProductTypeDTO productTypeDTO = ProductTypeDTO.builder()
                .id(productType.getId())
                .name(productType.getName())
                .createdAt(productType.getCreatedAt())
                .build();

        // Chuyển đổi Category sang CategoryDTO nhưng không bao gồm productTypes để tránh vòng lặp vô hạn
        if (productType.getCategory() != null) {
            CategoryDTO categoryDTO = CategoryDTO.builder()
                    .categoryId(productType.getCategory().getCategoryId())
                    .categoryName(productType.getCategory().getCategoryName())
                    .build();
            productTypeDTO.setCategory(categoryDTO);
        }

        // Chuyển đổi Products sang ProductDTO nếu có
        if (productType.getProducts() != null && !productType.getProducts().isEmpty()) {
            List<ProductDTO> productDTOs = productType.getProducts().stream()
                    .map(product -> ProductDTO.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            // Thêm các thuộc tính khác của Product mà bạn muốn chuyển đổi
                            // Không set productType ở đây để tránh vòng lặp vô hạn
                            .build())
                    .collect(Collectors.toList());
            productTypeDTO.setProducts(productDTOs);
        }

        return productTypeDTO;
    }

    // Phương thức chuyển đổi từ ProductTypeDTO sang ProductType
    public static ProductType convertProductTypeDTOToEntity(ProductTypeDTO productTypeDTO) {
        if (productTypeDTO == null) {
            return null;
        }

        ProductType productType = ProductType.builder()
                .id(productTypeDTO.getId())
                .name(productTypeDTO.getName())
                .createdAt(productTypeDTO.getCreatedAt())
                .build();

        // Chuyển đổi CategoryDTO sang Category
        if (productTypeDTO.getCategory() != null) {
            Category category = Category.builder()
                    .categoryId(productTypeDTO.getCategory().getCategoryId())
                    .categoryName(productTypeDTO.getCategory().getCategoryName())
                    .build();
            productType.setCategory(category);

            // Không thiết lập mối quan hệ productTypes trong category để tránh vòng lặp vô hạn
        }

        // Chuyển đổi ProductDTO sang Product nếu có
        if (productTypeDTO.getProducts() != null && !productTypeDTO.getProducts().isEmpty()) {
            List<Product> products = productTypeDTO.getProducts().stream()
                    .map(productDTO -> {
                        Product product = Product.builder()
                                .productId(productDTO.getProductId())
                                .name(productDTO.getName())
                                // Thêm các thuộc tính khác của Product mà bạn muốn chuyển đổi
                                .build();
                        // Thiết lập quan hệ ngược lại
                        product.setProductType(productType);
                        return product;
                    })
                    .collect(Collectors.toList());
            productType.setProducts(products);
        }

        return productType;
    }

    public static ProductTypeDTO convertProductTypeToDTOWithoutProducts(ProductType productType) {
        if (productType == null) {
            return null;
        }

        ProductTypeDTO productTypeDTO = ProductTypeDTO.builder()
                .id(productType.getId())
                .name(productType.getName())
                .createdAt(productType.getCreatedAt())
                .build();

        // Gán category nhưng KHÔNG bao gồm productTypes để tránh vòng lặp
        if (productType.getCategory() != null) {
            CategoryDTO categoryDTO = CategoryDTO.builder()
                    .categoryId(productType.getCategory().getCategoryId())
                    .categoryName(productType.getCategory().getCategoryName())
                    .build();
            productTypeDTO.setCategory(categoryDTO);
        }

        // KHÔNG set productTypeDTO.setProducts(...) để loại bỏ danh sách products

        return productTypeDTO;
    }


    /**
     * Chuyển đổi Product entity thành ProductDTO
     */
    public ProductDTO toProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO productDTO = ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .title(product.getTitle())
                .description(product.getDescription())
                .weight(product.getWeight())
                .currency(product.getCurrency())
                .minimalVariantPriceAmount(product.getMinimalVariantPriceAmount())
                .minimalQuantity(product.getMinimalQuantity())
                .availableForPurchase(product.getAvailableForPurchase())
                .build();

        // Chuyển đổi Seller sang SellerDTO nếu có
        if (product.getSeller() != null) {
            SellerDTO sellerDTO = SellerDTO.builder()
                    .sellerId(product.getSeller().getSellerId())
                    // Thêm các thuộc tính khác của Seller mà bạn muốn chuyển đổi
                    // Không set products ở đây để tránh vòng lặp vô hạn
                    .build();
            productDTO.setSeller(sellerDTO);
        }

        // Chuyển đổi ProductType sang ProductTypeDTO nếu có
        if (product.getProductType() != null) {
            ProductTypeDTO productTypeDTO = ProductTypeDTO.builder()
                    .id(product.getProductType().getId())
                    .name(product.getProductType().getName())
                    .createdAt(product.getProductType().getCreatedAt())
                    // Không set products ở đây để tránh vòng lặp vô hạn
                    .build();
            productDTO.setProductType(productTypeDTO);
        }

        // Chuyển đổi ProductVariants sang ProductVariantDTO nếu có
        if (product.getProductVariants() != null && !product.getProductVariants().isEmpty()) {
            List<ProductVariantDTO> productVariantDTOs = product.getProductVariants().stream()
                    .map(this::toProductVariantDTO)
                    .collect(Collectors.toList());
            productDTO.setProductVariants(productVariantDTOs);
        }

        // Chuyển đổi Reviews sang ReviewDTO nếu có
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            List<ReviewDTO> reviewDTOs = product.getReviews().stream()
                    .map(review -> ReviewDTO.builder()
                            .reviewId(review.getReviewId())
                            // Thêm các thuộc tính khác của Review mà bạn muốn chuyển đổi
                            // Không set product ở đây để tránh vòng lặp vô hạn
                            .build())
                    .collect(Collectors.toList());
            productDTO.setReviews(reviewDTOs);
        }

        // Chuyển đổi Wishlists sang WishlistDTO nếu có
        if (product.getWishlists() != null && !product.getWishlists().isEmpty()) {
            List<WishlistDTO> wishlistDTOs = product.getWishlists().stream()
                    .map(wishlist -> WishlistDTO.builder()
                            .wishlistId(wishlist.getWishlistId())
                            // Thêm các thuộc tính khác của Wishlist mà bạn muốn chuyển đổi
                            // Không set products ở đây để tránh vòng lặp vô hạn
                            .build())
                    .collect(Collectors.toList());
            productDTO.setWishlist(wishlistDTOs);
        }


        if(product.getCartItems() != null && !product.getCartItems().isEmpty()) {
            List<CartItemDTO> cartItemDTOs = product.getCartItems().stream()
                    .map(cartItem -> CartItemDTO.builder()
                            .cartItemId(cartItem.getCartItemId())
                            .quantity(cartItem.getQuantity())
                            .totalPriceItem(cartItem.getTotalPriceItem())
                            .unitPrice(cartItem.getUnitPrice())
                            .build())
                    .collect(Collectors.toList());
            productDTO.setCartItemDTOs(cartItemDTOs);
        }
        if(product.getOrderItems() != null && !product.getOrderItems().isEmpty()) {
            List<OrderItemDTO> orderItemDTOs = product.getOrderItems().stream()
                    .map(orderItem -> OrderItemDTO.builder()
                            .orderItemId(orderItem.getOrderItemId())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPrice())
                            .totalPriceItem(orderItem.getTotalPriceItem())
                            .build())
                    .collect(Collectors.toList());
            productDTO.setOrderItemDTOs(orderItemDTOs);
        }
        if(product.getProductFlashSales() != null && !product.getProductFlashSales().isEmpty()) {
            List<ProductFlashSaleDTO> productFlashSaleDTOs = product.getProductFlashSales().stream()
                    .map(productFlashSale -> ProductFlashSaleDTO.builder()
                            .id(productFlashSale.getId())
                            .flashSalePrice(productFlashSale.getFlashSalePrice())
                            .originalPrice(productFlashSale.getOriginalPrice())
                            .discountPercentage(productFlashSale.getDiscountPercentage())
                            .quota(productFlashSale.getQuota())
                            .soldCount(productFlashSale.getSoldCount())
                            .build())
                    .collect(Collectors.toList());
            productDTO.setProductFlashSales(productFlashSaleDTOs);
        }

        return productDTO;
    }

    public ProductDTO toOnLyProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO productDTO = ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .title(product.getTitle())
                .description(product.getDescription())
                .weight(product.getWeight())
                .currency(product.getCurrency())
                .minimalVariantPriceAmount(product.getMinimalVariantPriceAmount())
                .minimalQuantity(product.getMinimalQuantity())
                .availableForPurchase(product.getAvailableForPurchase())
                .build();

        return productDTO;
    }

    /**
     * Chuyển đổi ProductDTO thành Product entity
     */
    public Product toProductEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }

        Product product = Product.builder()
                .productId(productDTO.getProductId())
                .name(productDTO.getName())
                .title(productDTO.getTitle())
                .description(productDTO.getDescription())
                .weight(productDTO.getWeight())
                .currency(productDTO.getCurrency())
                .minimalVariantPriceAmount(productDTO.getMinimalVariantPriceAmount())
                .minimalQuantity(productDTO.getMinimalQuantity())
                .availableForPurchase(productDTO.getAvailableForPurchase())
                .build();

        // Chuyển đổi SellerDTO sang Seller nếu có
        if (productDTO.getSeller() != null) {
            Seller seller = Seller.builder()
                    .sellerId(productDTO.getSeller().getSellerId())
                    // Thêm các thuộc tính khác của Seller mà bạn muốn chuyển đổi
                    .build();
            product.setSeller(seller);
        }

        // Chuyển đổi ProductTypeDTO sang ProductType nếu có
        if (productDTO.getProductType() != null) {
            ProductType productType = ProductType.builder()
                    .id(productDTO.getProductType().getId())
                    .name(productDTO.getProductType().getName())
                    .createdAt(productDTO.getProductType().getCreatedAt())
                    .build();
            product.setProductType(productType);
        }

        // Chuyển đổi ProductVariantDTO sang ProductVariant nếu có
        if (productDTO.getProductVariants() != null && !productDTO.getProductVariants().isEmpty()) {
            List<ProductVariant> productVariants = productDTO.getProductVariants().stream()
                    .map(variantDTO -> {
                        ProductVariant variant = toProductVariantEntity(variantDTO);
                        variant.setProduct(product);
                        return variant;
                    })
                    .collect(Collectors.toList());
            product.setProductVariants(productVariants);
        }

        // Chuyển đổi ReviewDTO sang Review nếu có
        if (productDTO.getReviews() != null && !productDTO.getReviews().isEmpty()) {
            List<Review> reviews = productDTO.getReviews().stream()
                    .map(reviewDTO -> {
                        Review review = Review.builder()
                                .reviewId(reviewDTO.getReviewId())
                                // Thêm các thuộc tính khác của Review mà bạn muốn chuyển đổi
                                .build();
                        review.setProduct(product);
                        return review;
                    })
                    .collect(Collectors.toList());
            product.setReviews(reviews);
        }

        // Chuyển đổi WishlistDTO sang Wishlist nếu có
        if (productDTO.getWishlist() != null && !productDTO.getWishlist().isEmpty()) {
            List<Wishlist> wishlists = productDTO.getWishlist().stream()
                    .map(wishlistDTO -> Wishlist.builder()
                            .wishlistId(wishlistDTO.getWishlistId())
                            // Thêm các thuộc tính khác của Wishlist mà bạn muốn chuyển đổi
                            .build())
                    .collect(Collectors.toList());
            product.setWishlists(wishlists);
        }

        if(productDTO.getCartItemDTOs() != null && !productDTO.getCartItemDTOs().isEmpty()) {
            List<CartItem> cartItems = productDTO.getCartItemDTOs().stream()
                    .map(cartItemDTO -> {
                        CartItem cartItem = CartItem.builder()
                                .cartItemId(cartItemDTO.getCartItemId())
                                .quantity(cartItemDTO.getQuantity())
                                .totalPriceItem(cartItemDTO.getTotalPriceItem())
                                .unitPrice(cartItemDTO.getUnitPrice())
                                .build();
                        cartItem.setProduct(product);
                        return cartItem;
                    })
                    .collect(Collectors.toList());
            product.setCartItems(cartItems);
        }
        if(productDTO.getOrderItemDTOs() != null && !productDTO.getOrderItemDTOs().isEmpty()) {
            List<OrderItem> orderItems = productDTO.getOrderItemDTOs().stream()
                    .map(orderItemDTO -> OrderItem.builder()
                            .orderItemId(orderItemDTO.getOrderItemId())
                            .quantity(orderItemDTO.getQuantity())
                            .price(orderItemDTO.getPrice())
                            .totalPriceItem(orderItemDTO.getTotalPriceItem())
                            .build())
                    .collect(Collectors.toList());
            product.setOrderItems(orderItems);
        }
        if (productDTO.getProductFlashSales() != null && !productDTO.getProductFlashSales().isEmpty()) {
            List<ProductFlashSale> productFlashSales = productDTO.getProductFlashSales().stream()
                    .map(productFlashSaleDTO -> ProductFlashSale.builder()
                            .id(productFlashSaleDTO.getId())
                            .flashSalePrice(productFlashSaleDTO.getFlashSalePrice())
                            .originalPrice(productFlashSaleDTO.getOriginalPrice())
                            .discountPercentage(productFlashSaleDTO.getDiscountPercentage())
                            .quota(productFlashSaleDTO.getQuota())
                            .soldCount(productFlashSaleDTO.getSoldCount())
                            .build())
                    .collect(Collectors.toList());
            product.setProductFlashSales(productFlashSales);
        }

        return product;
    }

    /**
     * Chuyển đổi ProductVariant entity thành ProductVariantDTO
     */
    public ProductVariantDTO toProductVariantDTO(ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }

        ProductVariantDTO productVariantDTO = ProductVariantDTO.builder()
                .id(productVariant.getId())
                .name(productVariant.getName())
                .price(productVariant.getPrice())
                .quantity(productVariant.getQuantity())
                .weightVariant(productVariant.getWeightVariant())
                .build();

        // Chuyển đổi Product nếu có, nhưng chỉ thêm thông tin cơ bản để tránh vòng lặp vô hạn
        if (productVariant.getProduct() != null) {
            ProductDTO productDTO = ProductDTO.builder()
                    .productId(productVariant.getProduct().getProductId())
                    .name(productVariant.getProduct().getName())
                    // Không thêm productVariants để tránh vòng lặp vô hạn
                    .build();
            productVariantDTO.setProduct(productDTO);
        }

        // Chuyển đổi OrderItems nếu có
        if (productVariant.getOrderItems() != null && !productVariant.getOrderItems().isEmpty()) {
            List<OrderItemDTO> orderItemDTOs = productVariant.getOrderItems().stream()
                    .map(orderItem -> OrderItemDTO.builder()
                            // Thêm các thuộc tính cần thiết của OrderItem
                            .build())
                    .collect(Collectors.toList());
            productVariantDTO.setOrderItems(orderItemDTOs);
        } else {
            productVariantDTO.setOrderItems(new ArrayList<>());
        }
        if(productVariant.getCartItems() != null && !productVariant.getCartItems().isEmpty()) {
            List<CartItemDTO> cartItemDTOs = productVariant.getCartItems().stream()
                    .map(cartItem -> CartItemDTO.builder()
                            .cartItemId(cartItem.getCartItemId())
                            .quantity(cartItem.getQuantity())
                            .totalPriceItem(cartItem.getTotalPriceItem())
                            .unitPrice(cartItem.getUnitPrice())
                            .build())
                    .collect(Collectors.toList());
            productVariantDTO.setCartItemDTOs(cartItemDTOs);
        } else {
            productVariantDTO.setCartItemDTOs(new ArrayList<>());
        }

        return productVariantDTO;
    }

    /**
     * Chuyển đổi ProductVariantDTO thành ProductVariant entity
     */
    public ProductVariant toProductVariantEntity(ProductVariantDTO productVariantDTO) {
        if (productVariantDTO == null) {
            return null;
        }

        ProductVariant productVariant = ProductVariant.builder()
                .id(productVariantDTO.getId())
                .name(productVariantDTO.getName())
                .price(productVariantDTO.getPrice())
                .quantity(productVariantDTO.getQuantity())
                .weightVariant(productVariantDTO.getWeightVariant())
                .build();

        // Chuyển đổi Product nếu có, nhưng không đặt ProductVariants để tránh vòng lặp vô hạn
        if (productVariantDTO.getProduct() != null) {
            Product product = Product.builder()
                    .productId(productVariantDTO.getProduct().getProductId())
                    .name(productVariantDTO.getProduct().getName())
                    // Không thêm các mối quan hệ phức tạp
                    .build();
            productVariant.setProduct(product);
        }
        if (productVariantDTO.getOrderItems() != null && !productVariantDTO.getOrderItems().isEmpty()) {
            List<OrderItem> orderItems = productVariantDTO.getOrderItems().stream()
                    .map(orderItemDTO -> OrderItem.builder()
                            .build())
                    .collect(Collectors.toList());
            productVariant.setOrderItems(orderItems);
        } else {
            productVariant.setOrderItems(new ArrayList<>());
        }
        if(productVariantDTO.getCartItemDTOs() != null && !productVariantDTO.getCartItemDTOs().isEmpty()) {
            List<CartItem> cartItems = productVariantDTO.getCartItemDTOs().stream()
                    .map(cartItemDTO -> CartItem.builder()
                            .cartItemId(cartItemDTO.getCartItemId())
                            .quantity(cartItemDTO.getQuantity())
                            .totalPriceItem(cartItemDTO.getTotalPriceItem())
                            .unitPrice(cartItemDTO.getUnitPrice())
                            .build())
                    .collect(Collectors.toList());
            productVariant.setCartItems(cartItems);
        } else {
            productVariant.setCartItems(new ArrayList<>());
        }

        // Các danh sách OrderItems và WarehouseStocks nên được xử lý ở một layer khác
        // để tránh vòng lặp vô hạn và duy trì tính toàn vẹn của dữ liệu

        return productVariant;
    }

    /**
     * Chuyển đổi danh sách ProductVariant entities thành danh sách ProductVariantDTO
     */
    public List<ProductVariantDTO> toProductVariantDTOList(List<ProductVariant> productVariants) {
        if (productVariants == null) {
            return Collections.emptyList();
        }

        return productVariants.stream()
                .map(this::toProductVariantDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi danh sách ProductVariantDTO thành danh sách ProductVariant entities
     */
    public List<ProductVariant> toProductVariantEntityList(List<ProductVariantDTO> productVariantDTOs) {
        if (productVariantDTOs == null) {
            return Collections.emptyList();
        }

        return productVariantDTOs.stream()
                .map(this::toProductVariantEntity)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi danh sách Product entities thành danh sách ProductDTO
     */
    public List<ProductDTO> toProductDTOList(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }

        return products.stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi danh sách ProductDTO thành danh sách Product entities
     */
    public List<Product> toProductEntityList(List<ProductDTO> productDTOs) {
        if (productDTOs == null) {
            return Collections.emptyList();
        }

        return productDTOs.stream()
                .map(this::toProductEntity)
                .collect(Collectors.toList());
    }
}
