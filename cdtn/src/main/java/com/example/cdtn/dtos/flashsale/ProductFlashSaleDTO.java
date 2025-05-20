package com.example.cdtn.dtos.flashsale;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.entity.flashsale.FlashSale;
import com.example.cdtn.entity.products.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFlashSaleDTO {
    private Long id;
    private ProductDTO product;
    private FlashSaleDTO flashSale;
    private Double flashSalePrice;
    private Double originalPrice;
    private Double discountPercentage;
    @NotNull(message = "Quota không thể rỗng")
    @Positive(message = "Quota phải là lớn hơn 0")
    private Integer quota;
    private Integer soldCount;
    private Boolean isActive;
}
