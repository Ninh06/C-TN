package com.example.cdtn.dtos.flashsale;

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
public class AddProductFlashSaleRequest {
    @NotNull(message = "productId là bắt buộc")
    @Positive(message = "productId phải lớn hơn 0")
    private Long productId;

    @NotNull(message = "quota là bắt buộc")
    @Positive(message = "quota phải lớn hơn 0")
    private Integer quota;

    @NotNull(message = "discountPercentage là bắt buộc")
    @Positive(message = "discountPercentage phải lớn hơn 0")
    private Double discountPercentage;
}
