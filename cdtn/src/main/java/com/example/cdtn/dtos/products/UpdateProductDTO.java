package com.example.cdtn.dtos.products;

import com.example.cdtn.dtos.orders.OrderItemDTO;
import com.example.cdtn.dtos.shopcart.CartItemDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProductDTO {

    private Long productId;

    private String name;

    private String title;

    private String description;

    private Double weight;

    private String currency;

    private Double minimalVariantPriceAmount;

    private Long minimalQuantity;

    private Boolean availableForPurchase;
}
