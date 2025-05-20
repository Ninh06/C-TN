package com.example.cdtn.dtos.discounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateDiscountVoucherDTO {
    private String voucherCode;
    private Double discountPercentage;
    private Date startDate;
    private Date endDate;
    private int quantityVoucher;
    private Boolean oncePerCustomer;
}
