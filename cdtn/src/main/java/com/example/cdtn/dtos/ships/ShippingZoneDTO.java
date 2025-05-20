package com.example.cdtn.dtos.ships;

import com.example.cdtn.dtos.ships.ShippingMethodDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingZoneDTO {

    private Long id;

    @NotBlank(message = "Tên khu vực vận chuyển không được để trống")
    @Size(max = 255, message = "Tên khu vực vận chuyển tối đa 255 ký tự")
    private String zoneName;

    @Size(max = 500, message = "Mô tả khu vực vận chuyển tối đa 500 ký tự")
    private String description;

    private List<ShippingMethodDTO> shippingMethods;
}
