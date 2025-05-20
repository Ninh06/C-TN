package com.example.cdtn.dtos.users;

import com.example.cdtn.dtos.AddressDTO;
import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.warehouse.WarehouseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellerDTO {

    private Long sellerId;

//    private UserDTO user;

    private Date createdAt;

    private List<ProductDTO> products;

    private List<OrderDTO> orderDTOS;

    private WarehouseDTO warehouse;

    private AddressDTO address;
}

