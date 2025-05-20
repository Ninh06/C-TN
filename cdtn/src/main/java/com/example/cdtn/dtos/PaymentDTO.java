package com.example.cdtn.dtos;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO {

    private Long paymentId;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private String paymentDes;

    private Date createdAt;

}
