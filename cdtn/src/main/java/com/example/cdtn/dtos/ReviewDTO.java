package com.example.cdtn.dtos;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {

    private Long reviewId;

    private ProductDTO product;

    private BuyerDTO buyer;

    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating phải từ 1 đến 5")
    @Max(value = 5, message = "Rating phải từ 1 đến 5")
    private Integer rating;

    private String reviewText;

    private Date createdAt;
}
