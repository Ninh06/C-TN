package com.example.cdtn.dtos.auth;

import com.example.cdtn.dtos.users.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private String message;
    private String token;
    private String userType;
}
