package com.example.cdtn.dtos.discounts;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountDetailDTO {

    private Long discountDetailId;

    private DiscountDTO discount;

    private DiscountVoucherDTO discountVoucherDTO;

    private OrderDTO order;

    @NotNull(message = "Discounted amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discounted amount must be greater than 0")
    private Double discountedAmount;

    private Date createdAt;
}
