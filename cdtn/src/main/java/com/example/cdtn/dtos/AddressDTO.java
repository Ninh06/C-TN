package com.example.cdtn.dtos;

import com.example.cdtn.dtos.users.SellerDTO;
import com.example.cdtn.dtos.warehouse.WarehouseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

    private Long id;

    @NotBlank(message = "Địa chỉ đường 1 không được để trống")
    private String streetAddress1;

    private String streetAddress2;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Mã bưu điện không được để trống")
    private String postalCode;

    @NotBlank(message = "Quốc gia không được để trống")
    private String country;

    private String countryArea;

    private String phone;

    private String cityArea;

    private SellerDTO seller;

    private WarehouseDTO warehouse;
}
