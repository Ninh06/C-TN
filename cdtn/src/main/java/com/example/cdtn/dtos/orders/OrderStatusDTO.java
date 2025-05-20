package com.example.cdtn.dtos.orders;

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
public class OrderStatusDTO {

    private Long orderStatusId;

    @NotBlank(message = "Mô tả trạng thái đơn hàng không được để trống")
    private String orderStatusDesc;

    private Date createdAt;

    private List<OrderDTO> orders;

    private List<ReturnOrderDTO> returnOrders;
}
