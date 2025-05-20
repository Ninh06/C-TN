package com.example.cdtn.controller;

import com.example.cdtn.dtos.orders.ReturnOrderDTO;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.service.impl.ReturnOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return-orders")
@RequiredArgsConstructor
public class ReturnOrderController {
    @Autowired
    private ReturnOrderService returnOrderService;

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> createReturnOrder(@RequestBody ReturnOrderDTO returnOrderDTO) {
        try {
            ReturnOrderDTO createdReturnOrder = returnOrderService.createReturnOrder(returnOrderDTO);
            return new ResponseEntity<>(createdReturnOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (OurException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorResponse("Lỗi server: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** Lấy danh sách đơn trả hàng của một khách hàng*/
    @GetMapping("/buyer/{buyerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUYER')")
    public ResponseEntity<?> getReturnOrdersByBuyerId(@PathVariable Long buyerId) {
        try {
            List<ReturnOrderDTO> returnOrders = returnOrderService.getReturnOrdersByBuyerId(buyerId);
            return ResponseEntity.ok(returnOrders);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse("Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /**Lấy chi tiết đơn trả hàng theo ID*/
    @GetMapping("/{returnId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReturnOrderById(@PathVariable Long returnId) {
        try {
            ReturnOrderDTO returnOrderDTO = returnOrderService.getReturnOrderById(returnId);
            return ResponseEntity.ok(returnOrderDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse("Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /**Lấy đơn trả hàng theo ID đơn hàng gốc*/
    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReturnOrderByOrderId(@PathVariable Long orderId) {
        try {
            ReturnOrderDTO returnOrderDTO = returnOrderService.getReturnOrderByOrderId(orderId);
            return ResponseEntity.ok(returnOrderDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse("Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /**Cập nhật trạng thái đơn trả hàng*/
    @PutMapping("/{returnId}/status/{statusId}")
    public ResponseEntity<?> updateReturnOrderStatus(
            @PathVariable Long returnId,
            @PathVariable Long statusId) {
        try {
            ReturnOrderDTO updatedReturnOrderDTO = returnOrderService.updateReturnOrderStatus(returnId, statusId);
            return ResponseEntity.ok(updatedReturnOrderDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse("Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /**Lấy danh sách đơn trả hàng theo trạng thái*/
    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<ReturnOrderDTO>> getReturnOrdersByStatus(@PathVariable Long statusId) {
        List<ReturnOrderDTO> returnOrders = returnOrderService.getReturnOrdersByStatus(statusId);
        return ResponseEntity.ok(returnOrders);
    }

    // Class để trả về thông báo lỗi
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
