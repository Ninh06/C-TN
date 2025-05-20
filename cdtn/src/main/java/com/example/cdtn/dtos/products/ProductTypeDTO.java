package com.example.cdtn.dtos.products;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTypeDTO {

    private Long id;

    @NotBlank(message = "Tên loại sản phẩm không được để trống")
    private String name;

    private Date createdAt;

    private CategoryDTO category;

    private List<ProductDTO> products;

}
