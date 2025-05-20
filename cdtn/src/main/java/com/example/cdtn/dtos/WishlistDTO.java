package com.example.cdtn.dtos;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
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
public class WishlistDTO {

    private Long wishlistId;

    private Long buyerId;

    private List<WishlistItemDTO> products;

    private Date createdAt;
}
