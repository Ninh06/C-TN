package com.example.cdtn.dtos.warehouse;

import com.example.cdtn.dtos.AddressDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseDTO {

    private Long id;

    @NotBlank(message = "Warehouse name is required")
    private String nameWarehouse;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private AddressDTO address;

    private SellerDTO seller;

}
