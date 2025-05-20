package com.example.cdtn.dtos.shopcart;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemDTO {

    private Long cartItemId;

    private com.example.cdtn.dtos.shopcart.ShoppingCartDTO shoppingCart;

    @NotNull(message = "Số lượng sản phẩm không được để trống")
    private Integer quantity;

    @NotNull(message = "Tổng giá trị mục giỏ hàng không được để trống")
    private BigDecimal totalPriceItem;

    @NotNull(message = "Đơn giá sản phẩm không được để trống")
    private BigDecimal unitPrice;

//    @NotNull(message = "Cân nặng đơn sản phẩm không được để trống")
//    private Double unitWeight;
//
//    @NotNull(message = "Tổng cân nặng sản phẩm không được để trống")
//    private Double totalWeight;

    private ProductDTO product;

    private ProductVariantDTO productVariant;

    private Date createdAt;
}
