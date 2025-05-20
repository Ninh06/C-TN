package com.example.cdtn.dtos.orders;

import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOrderRequest {
    private Long buyerId;
    private Long sellerId;
    private Long shippingAddressId;
    private Long shippingMethodId;
    private Long voucherId;
    private Long discountId;
    private List<Map<String, Object>> orderItems;
}
