package com.example.cdtn.dtos.products;

import com.example.cdtn.dtos.orders.OrderItemDTO;
import com.example.cdtn.dtos.shopcart.CartItemDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVariantDTO {

    private Long id;

    @NotBlank(message = "Tên biến thể sản phẩm không được để trống")
    private String name;

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    private Long quantity;

    @NotNull(message = "Cân nặng không được để trống")
    private Double weightVariant;


    private BigDecimal originalPrice;
    private BigDecimal flashSalePrice;
    private Double discountPercentage;
    private Boolean isInFlashSale;
    private Integer flashSaleQuota;
    private Integer flashSaleSoldCount;


    private ProductDTO product;

    private List<OrderItemDTO> orderItems;

    private List<CartItemDTO> cartItemDTOs;
}
