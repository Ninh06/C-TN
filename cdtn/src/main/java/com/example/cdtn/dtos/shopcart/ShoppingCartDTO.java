package com.example.cdtn.dtos.shopcart;

import com.example.cdtn.dtos.users.BuyerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartDTO {

    private Long cartId;

    private BuyerDTO buyer;

    private List<com.example.cdtn.dtos.shopcart.CartItemDTO> cartItems;

    @NotNull(message = "Tổng giá trị giỏ hàng không được để trống")
    private BigDecimal totalPrice;

    private Date createdAt;
}
