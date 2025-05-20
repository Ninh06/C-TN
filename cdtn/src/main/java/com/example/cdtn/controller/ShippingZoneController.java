package com.example.cdtn.controller;

import com.example.cdtn.dtos.ships.ShippingZoneDTO;
import com.example.cdtn.service.impl.ShippingZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-zones")
@RequiredArgsConstructor
public class ShippingZoneController {
    @Autowired
    private ShippingZoneService shippingZoneService;

    /** Lấy tất cả shipping zones*/
    @GetMapping
    public ResponseEntity<List<ShippingZoneDTO>> getAllShippingZones() {
        List<ShippingZoneDTO> zones = shippingZoneService.getAllShippingZones();
        return ResponseEntity.ok(zones);
    }

    /** Lấy shipping zone theo ID*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getShippingZoneById(@PathVariable Long id) {
        try {
            ShippingZoneDTO zone = shippingZoneService.getShippingZoneById(id);
            return ResponseEntity.ok(zone);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Tạo shipping zone mới*/
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createShippingZone(@RequestBody ShippingZoneDTO shippingZoneDTO) {
        try {
            ShippingZoneDTO created = shippingZoneService.createShippingZone(shippingZoneDTO);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi khi tạo Shipping Zone: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Cập nhật shipping zone*/
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShippingZone(@PathVariable Long id,
                                                @RequestBody ShippingZoneDTO shippingZoneDTO) {
        try {
            ShippingZoneDTO updated = shippingZoneService.updateShippingZone(id, shippingZoneDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi khi cập nhật vùng vận chuyển: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Xoá shipping zone*/
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteShippingZone(@PathVariable Long id) {
        try {
            shippingZoneService.deleteShippingZone(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi khi xóa vùng vận chuyển: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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
