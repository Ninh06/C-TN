package com.example.cdtn.controller;

import com.example.cdtn.dtos.orders.OrderStatusDTO;
import com.example.cdtn.service.impl.OrderStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order-statuses")
@RequiredArgsConstructor
public class OrderStatusController {
    @Autowired
    private  OrderStatusService orderStatusService;

    @GetMapping
    public ResponseEntity<List<OrderStatusDTO>> getAllOrderStatuses() {
        List<OrderStatusDTO> orderStatuses = orderStatusService.getAllOrderStatuses();
        return ResponseEntity.ok(orderStatuses);
    }

    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getOrderStatusesWithPagination(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "orderStatusId") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderStatusDTO> orderStatusPage = orderStatusService.getAllOrderStatusesWithPagination(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", orderStatusPage.getContent());
        response.put("page", orderStatusPage.getNumber());
        response.put("size", orderStatusPage.getSize());
        response.put("totalElements", orderStatusPage.getTotalElements());
        response.put("totalPages", orderStatusPage.getTotalPages());
        response.put("last", orderStatusPage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrderStatusById(@PathVariable Long id) {
        try {
            OrderStatusDTO orderStatus = orderStatusService.getOrderStatusById(id);
            return ResponseEntity.ok(orderStatus);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse("Lỗi khi lấy trạng thái đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi không xác định: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderStatusDTO>> searchOrderStatusByDescription(
            @RequestParam("description") String description) {
        List<OrderStatusDTO> orderStatuses = orderStatusService.findByDescription(description);
        return ResponseEntity.ok(orderStatuses);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderStatusDTO> createOrderStatus(@Valid @RequestBody OrderStatusDTO orderStatusDTO) {
        OrderStatusDTO createdOrderStatus = orderStatusService.createOrderStatus(orderStatusDTO);
        return new ResponseEntity<>(createdOrderStatus, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusDTO orderStatusDTO) {
        try {
            OrderStatusDTO updatedOrderStatus = orderStatusService.updateOrderStatus(id, orderStatusDTO);
            return ResponseEntity.ok(updatedOrderStatus);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteOrderStatus(@PathVariable Long id) {
        try {
            orderStatusService.deleteOrderStatus(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Trạng thái đơn hàng đã được xóa thành công");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse("Lỗi khi xóa trạng thái đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Lỗi máy chủ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
