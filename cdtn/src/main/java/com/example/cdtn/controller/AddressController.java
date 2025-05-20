package com.example.cdtn.controller;

import com.example.cdtn.dtos.AddressDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.example.cdtn.dtos.warehouse.WarehouseDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    @Autowired
    private AddressService addressService;

    /**API lấy tất cả địa chỉ*/
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAddresses() {
        try {
            List<AddressDTO> addresses = addressService.getAllAddresses();
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể lấy danh sách địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**API lấy địa chỉ theo ID*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id) {
        try {
            AddressDTO addressDTO = addressService.getAddressById(id);
            return ResponseEntity.ok(addressDTO);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể lấy địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**API lấy địa chỉ theo Seller ID*/
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getAddressBySellerId(@PathVariable Long sellerId) {
        try {
            AddressDTO addressDTO = addressService.getAddressBySellerId(sellerId);
            return ResponseEntity.ok(addressDTO);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể lấy địa chỉ của người bán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**lấy địa chỉ theo Warehouse ID*/
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<?> getAddressByWarehouseId(@PathVariable Long warehouseId) {
        try {
            AddressDTO addressDTO = addressService.getAddressByWarehouseId(warehouseId);
            return ResponseEntity.ok(addressDTO);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể lấy địa chỉ của kho hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**API tạo địa chỉ mới*/
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> createAddress(@RequestBody AddressDTO addressDTO) {
        try {
            AddressDTO createdAddress = addressService.createAddress(addressDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể tạo địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**API cập nhật địa chỉ*/
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        try {
            AddressDTO updatedAddress = addressService.updateAddress(id, addressDTO);
            return ResponseEntity.ok(updatedAddress);
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể cập nhật địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**API xóa địa chỉ*/
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (OurException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không thể xóa địa chỉ: " + e.getMessage());
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
