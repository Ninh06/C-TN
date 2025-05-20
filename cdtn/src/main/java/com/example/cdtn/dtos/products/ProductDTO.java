package com.example.cdtn.dtos.products;

import com.example.cdtn.dtos.flashsale.ProductFlashSaleDTO;
import com.example.cdtn.dtos.orders.OrderItemDTO;
import com.example.cdtn.dtos.orders.ReturnOrderDTO;
import com.example.cdtn.dtos.shopcart.CartItemDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {

    private Long productId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "Tiêu đề sản phẩm không được để trống")
    private String title;

    private String description;

    @NotNull(message = "Trọng lượng sản phẩm không được để trống")
    private Double weight;

    private String currency;

    @NotNull(message = "Giá của biến thể sản phẩm tối thiểu không được để trống")
    private Double minimalVariantPriceAmount;

    private Long minimalQuantity;

    @NotNull(message = "Trạng thái sản phẩm có thể mua được không không được để trống")
    private Boolean availableForPurchase;


    // Các thuộc tính lflash sale
    private BigDecimal originalPrice;
    private BigDecimal flashSalePrice;
    private Double discountPercentage;
    private Boolean isInFlashSale;
    private Integer flashSaleQuota;
    private Integer flashSaleSoldCount;


    private SellerDTO seller;

    private ProductTypeDTO productType;

    private List<ProductVariantDTO> productVariants;

    private List<CartItemDTO> cartItemDTOs;

    private List<OrderItemDTO> orderItemDTOs;

    private List<com.example.cdtn.dtos.ReviewDTO> reviews;

    private List<com.example.cdtn.dtos.WishlistDTO> wishlist;

    private List<ProductFlashSaleDTO> productFlashSales;

}
