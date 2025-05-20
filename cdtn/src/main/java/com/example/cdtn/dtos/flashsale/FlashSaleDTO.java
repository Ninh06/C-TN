package com.example.cdtn.dtos.flashsale;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlashSaleDTO {
    private Long flashSaleId;
    @NotBlank(message = "Tên không được để trống")
    private String name;
    private String description;
    @NotNull(message = "Thời gian bắt đầu không thể là null")
    private Date startTime;
    @NotNull(message = "Thời gian kết thúc không thể là null")
    private Date endTime;
    private String status;
    private Boolean isActive;
    private List<ProductFlashSaleDTO> productFlashSales;
}
