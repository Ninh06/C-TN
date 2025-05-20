package com.example.cdtn.dtos.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long userId;

    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(max = 50, message = "Tên người dùng tối đa 50 ký tự")
    private String userName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    private Date createdAt;

    @NotBlank(message = "Loại người dùng không được để trống")
    private String userType;

    private BuyerDTO buyer;
    private SellerDTO seller;
}
