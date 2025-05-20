package com.example.cdtn.controller;

import com.example.cdtn.dtos.discounts.DiscountVoucherDTO;
import com.example.cdtn.dtos.discounts.UpdateDiscountVoucherDTO;
import com.example.cdtn.entity.discounts.DiscountVoucher;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.DiscountMapper;
import com.example.cdtn.service.impl.DiscountVoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discount-vouchers")
@RequiredArgsConstructor
public class DiscountVoucherController {

    @Autowired
    private final DiscountVoucherService discountVoucherService;

    @Autowired
    private DiscountMapper discountMapper;

    /**API lấy danh sách tất cả voucher*/
    @GetMapping
    public ResponseEntity<List<DiscountVoucherDTO>> getAllVouchers() {
        List<DiscountVoucherDTO> vouchers = discountVoucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }

    /**API lấy danh sách các voucher đang có hiệu lực*/
    @GetMapping("/active")
    public ResponseEntity<List<DiscountVoucherDTO>> getActiveVouchers() {
        List<DiscountVoucherDTO> activeVouchers = discountVoucherService.getActiveVouchers();
        return ResponseEntity.ok(activeVouchers);
    }

    /**API lấy thông tin một voucher theo ID*/
    @GetMapping("/{voucherId}")
    public ResponseEntity<?> getVoucherById(@PathVariable Long voucherId) {
        try {
            DiscountVoucherDTO voucher = discountVoucherService.getVoucherById(voucherId);
            return ResponseEntity.ok(voucher);
        } catch (OurException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi lấy thông tin voucher.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**API lấy thông tin voucher theo mã code*/
    @GetMapping("/code/{voucherCode}")
    public ResponseEntity<?> getVoucherByCode(@PathVariable String voucherCode) {
        try {
            DiscountVoucherDTO voucher = discountVoucherService.getVoucherByCode(voucherCode);
            return ResponseEntity.ok(voucher);
        } catch (OurException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi tìm kiếm voucher.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**API tạo mới một voucher*/
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> createVoucher(@Valid @RequestBody DiscountVoucherDTO discountVoucherDTO) {
        try {
            DiscountVoucherDTO createdVoucher = discountVoucherService.createVoucher(discountVoucherDTO);
            return new ResponseEntity<>(createdVoucher, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            // Trường hợp mã voucher đã tồn tại
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            // Các lỗi không xác định khác
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi tạo voucher.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**API cập nhật thông tin voucher*/
    @PutMapping("/{voucherId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> updateVoucher(
            @PathVariable Long voucherId,
            @RequestBody UpdateDiscountVoucherDTO updateDTO) {
        try {
            DiscountVoucherDTO updatedVoucher = discountVoucherService.updateVoucher(voucherId, updateDTO);
            return ResponseEntity.ok(updatedVoucher);
        } catch (IllegalArgumentException ex) {
            // Trường hợp mã voucher bị trùng
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (OurException ex) {
            // Không tìm thấy voucher theo ID
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            // Lỗi không xác định
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi cập nhật voucher.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**API xóa một voucher*/
    @DeleteMapping("/{voucherId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> deleteVoucher(@PathVariable Long voucherId) {
        try {
            discountVoucherService.deleteVoucher(voucherId);
            return ResponseEntity.ok(Map.of("message", "Xóa voucher thành công"));
        } catch (OurException ex) {
            // Không tìm thấy voucher theo ID
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            // Các lỗi không xác định khác
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi xóa voucher.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**API kiểm tra tính hợp lệ của voucher*/
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateVoucher(
            @RequestParam String voucherCode,
            @RequestParam(required = false) Long customerId) {
        boolean isValid = discountVoucherService.isVoucherValid(voucherCode, customerId);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    /**API tính toán giá trị giảm giá khi áp dụng voucher*/
    @GetMapping("/calculate-discount")
    public ResponseEntity<Map<String, Double>> calculateDiscount(
            @RequestParam String voucherCode,
            @RequestParam double orderAmount) {
        double discountAmount = discountVoucherService.applyVoucher(voucherCode, orderAmount);
        return ResponseEntity.ok(Map.of(
                "orderAmount", orderAmount,
                "discountAmount", discountAmount,
                "finalAmount", orderAmount - discountAmount
        ));
    }

    /**API tìm các voucher có phần trăm giảm giá lớn hơn hoặc bằng giá trị cung cấp*/
    @GetMapping("/by-percentage")
    public ResponseEntity<List<DiscountVoucherDTO>> getVouchersByPercentage(@RequestParam Double percentage) {
        // Lấy danh sách voucher từ repository
        List<DiscountVoucher> vouchers = discountVoucherService.getDiscountVoucherRepository()
                .findByDiscountPercentageGreaterThanEqual(percentage);

        // Chuyển đổi sang DTO
        List<DiscountVoucherDTO> voucherDTOs = vouchers.stream()
                .map(discountMapper::toDiscountVoucherDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(voucherDTOs);
    }

    /**API xóa các voucher đã hết hạn */
    @DeleteMapping("/expired")
    public ResponseEntity<Map<String, Integer>> deleteExpiredVouchers() {
        Date currentDate = new Date();
        int deletedCount = discountVoucherService.getDiscountVoucherRepository()
                .deleteExpiredVouchers(currentDate);
        return ResponseEntity.ok(Map.of("deletedCount", deletedCount));
    }

    /**API đếm số lần sử dụng của voucher */
    @GetMapping("/usage-count/{voucherCode}")
    public ResponseEntity<Map<String, Long>> getVoucherUsageCount(@PathVariable String voucherCode) {
        Long usageCount = discountVoucherService.getDiscountVoucherRepository()
                .countVoucherUsage(voucherCode);
        return ResponseEntity.ok(Map.of("usageCount", usageCount));
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
