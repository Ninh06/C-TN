package com.example.cdtn.controller;

import com.example.cdtn.dtos.PaymentDTO;
import com.example.cdtn.service.impl.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable("id") Long id) {
        try {
            PaymentDTO paymentDTO = paymentService.getPaymentById(id);
            return ResponseEntity.ok(paymentDTO);
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePayment(@PathVariable("id") Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return buildErrorResponse(e.getMessage());
        }
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
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
