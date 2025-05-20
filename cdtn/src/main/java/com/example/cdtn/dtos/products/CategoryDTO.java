package com.example.cdtn.dtos.products;

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
public class CategoryDTO {

    private Long categoryId;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String categoryName;

//    private Date createdAt;

    private List<ProductTypeDTO> productTypes;
}
