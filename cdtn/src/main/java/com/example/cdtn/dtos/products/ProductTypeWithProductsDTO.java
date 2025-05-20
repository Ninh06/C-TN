package com.example.cdtn.dtos.products;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTypeWithProductsDTO {
    private Long productTypeId;
    private String productTypeName;
    private List<ProductDTO> products;
}
