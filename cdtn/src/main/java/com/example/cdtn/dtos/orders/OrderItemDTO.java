package com.example.cdtn.dtos.orders;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDTO {

    private Long orderItemId;

    private OrderDTO order;

    private ProductDTO productDTO;

    private ProductVariantDTO productVariant;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    private BigDecimal totalPriceItem;

    @NotNull(message = "Thời gian tạo không được để trống")
    private Date createdAt;
}
