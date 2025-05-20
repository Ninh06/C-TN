package com.example.cdtn.dtos.users;

import com.example.cdtn.dtos.PaymentDTO;
import com.example.cdtn.dtos.WishlistDTO;
import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.orders.ReturnOrderDTO;
import com.example.cdtn.dtos.ships.ShippingAddressDTO;
import com.example.cdtn.dtos.shopcart.ShoppingCartDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyerDTO {

    private Long buyerId;

    private UserDTO user;

    @NotNull(message = "Ngày tạo không được để trống")
    private Date createdAt;

    private List<ShippingAddressDTO> shippingAddresses;

    private WishlistDTO wishlist;

    private ShoppingCartDTO shoppingCart;

    private List<OrderDTO> orders;

    private List<com.example.cdtn.dtos.ReviewDTO> reviews;

    private List<ReturnOrderDTO> returnOrders;


}
