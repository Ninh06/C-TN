package com.example.cdtn.controller;

import com.example.cdtn.dtos.auth.RegistrationRequestDTO;
import com.example.cdtn.dtos.users.UpdateUserRequestDTO;
import com.example.cdtn.dtos.users.UserDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.BuyerService;
import com.example.cdtn.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BuyerService buyerService;

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        try {
            UserDTO userDTO = userService.getUserById(userId);
            return ResponseEntity.ok(userDTO);
        } catch (OurException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDTO request) {
        try {
            UserDTO updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("Xóa người dùng thành công với ID: " + id);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String userType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<UserDTO> result = userService.searchUsers(userName, email, fullName, userType, pageable);

        return ResponseEntity.ok(result);
    }


    @PutMapping("/{userId}/convert-to-seller")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUYER')")
    public ResponseEntity<?> convertBuyerToSeller(@PathVariable Long userId,
                                                  @RequestBody RegistrationRequestDTO request) {
        try {
            UserDTO updatedUser = userService.convertBuyerToSeller(userId, request);
            return ResponseEntity.ok(updatedUser);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse("Lỗi khi chuyển đổi Buyer thành Seller: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Đã xảy ra lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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
