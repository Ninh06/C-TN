package com.example.cdtn.dtos.ships;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingAddressDTO {

    private Long id;

    @NotBlank(message = "Họ không được để trống")
    @Size(max = 50, message = "Họ không được vượt quá 50 ký tự")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 50, message = "Tên không được vượt quá 50 ký tự")
    private String lastName;

    @Size(max = 100, message = "Tên người nhận không được vượt quá 100 ký tự")
    private String personName;

    @NotBlank(message = "Địa chỉ số 1 không được để trống")
    @Size(max = 255, message = "Địa chỉ số 1 không được vượt quá 255 ký tự")
    private String streetAddress1;

    @Size(max = 255, message = "Địa chỉ số 2 không được vượt quá 255 ký tự")
    private String streetAddress2;

    @NotBlank(message = "Thành phố không được để trống")
    @Size(max = 100, message = "Thành phố không được vượt quá 100 ký tự")
    private String city;

    @NotBlank(message = "Mã bưu chính không được để trống")
    @Size(max = 20, message = "Mã bưu chính không được vượt quá 20 ký tự")
    private String postalCode;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 100, message = "Quốc gia không được vượt quá 100 ký tự")
    private String country;

    @Size(max = 100, message = "Khu vực quốc gia không được vượt quá 100 ký tự")
    private String countryArea;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 10, message = "Số điện thoại không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[0-9+\\-()\\s]*$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Size(max = 100, message = "Khu vực thành phố không được vượt quá 100 ký tự")
    private String cityArea;

    private BuyerDTO buyer;

    private List<OrderDTO> orders;
}
