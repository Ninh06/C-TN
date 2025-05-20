package com.example.cdtn.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationRequestDTO {
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải từ 3 đến 50 ký tự")
    private String userName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Loại người dùng không được để trống")
    @Pattern(regexp = "BUYER|SELLER", message = "Loại người dùng phải là BUYER hoặc SELLER")
    private String userType;

    // Các trường địa chỉ dành cho Seller
    private String nameWarehouse;
    private String companyName;
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String postalCode;
    private String country;
    private String countryArea;
    private String phone;
    private String cityArea;

}
