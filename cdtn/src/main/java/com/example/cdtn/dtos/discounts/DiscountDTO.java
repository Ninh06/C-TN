package com.example.cdtn.dtos.discounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountDTO {

    private Long discountId;

    @NotBlank(message = "Discount name is required")
    private String discountName;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "1.0", message = "Discount percentage must be less than or equal to 1")
    private BigDecimal discountPercentage;

    @NotNull(message = "Start date is required")
    private Date startDate;

    @NotNull(message = "End date is required")
    private Date endDate;

    @DecimalMin(value = "0.0", message = "Minimum order value must be non-negative")
    private BigDecimal minOrderValue;

    private Date createdAt;

    private List<DiscountDetailDTO> discountDetails;
}
