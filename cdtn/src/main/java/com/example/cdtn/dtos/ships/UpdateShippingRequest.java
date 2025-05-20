package com.example.cdtn.dtos.ships;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateShippingRequest {
    private Long orderId;
    private Long shippingAddressId;
    private Long shippingMethodId;
}
