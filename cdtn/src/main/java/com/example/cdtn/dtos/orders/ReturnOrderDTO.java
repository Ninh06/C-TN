package com.example.cdtn.dtos.orders;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnOrderDTO {

    private Long returnId;

    private BuyerDTO buyer;

    private OrderDTO order;

    @NotBlank(message = "Lý do trả hàng không được để trống")
    private String reason;

    private OrderStatusDTO orderStatus;

    private Date createdAt;
}
