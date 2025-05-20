package com.example.cdtn.dtos.discounts;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountVoucherDTO {

    private Long voucherId;

    @NotBlank(message = "Voucher code is required")
    @Size(max = 50, message = "Voucher code must be at most 50 characters")
    private String voucherCode;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount percentage must be greater than 0")
    private Double discountPercentage;

    @NotNull(message = "Start date is required")
    private Date startDate;

    @NotNull(message = "End date is required")
    private Date endDate;

    @NotNull(message = "Quantity voucher flag is required")
    private int quantityVoucher;

    @NotNull(message = "Once per customer flag is required")
    private Boolean oncePerCustomer;

    private Date createdAt;

    private Date updatedAt;

    private List<OrderDTO> orders;

    private List<DiscountDetailDTO> discountDetailDTOs;
}
