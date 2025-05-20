package com.example.cdtn.controller;

import com.example.cdtn.dtos.auth.LoginRequestDTO;
import com.example.cdtn.dtos.auth.LoginResponseDTO;
import com.example.cdtn.dtos.auth.RegistrationRequestDTO;
import com.example.cdtn.dtos.users.UserDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequestDTO request) {
        try {
            UserDTO registeredUser = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Đã xảy ra lỗi trong quá trình đăng ký: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponse = userService.login(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
