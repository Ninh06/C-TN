package com.example.cdtn.controller;

import com.example.cdtn.dtos.invoices.InvoiceStatusDTO;
import com.example.cdtn.service.impl.InvoiceStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-statuses")
@RequiredArgsConstructor
public class InvoiceStatusController {
    @Autowired
    private InvoiceStatusService invoiceStatusService;

    // Lấy tất cả trạng thái hóa đơn
    @GetMapping
    public ResponseEntity<List<InvoiceStatusDTO>> getAllInvoiceStatuses() {
        List<InvoiceStatusDTO> statusList = invoiceStatusService.getAllInvoiceStatuses();
        return ResponseEntity.ok(statusList);
    }

    // Lấy một trạng thái hóa đơn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceStatusById(@PathVariable("id") Long id) {
        try {
            InvoiceStatusDTO status = invoiceStatusService.getInvoiceStatusById(id);
            return ResponseEntity.ok(status);
        } catch (RuntimeException ex) {
            // Trường hợp không tìm thấy hoặc lỗi nghiệp vụ
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            // Các lỗi hệ thống khác
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi lấy trạng thái hóa đơn.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // Tạo mới trạng thái hóa đơn
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceStatusDTO> createInvoiceStatus(@RequestBody InvoiceStatusDTO invoiceStatusDTO) {
        InvoiceStatusDTO created = invoiceStatusService.createInvoiceStatus(invoiceStatusDTO);
        return ResponseEntity.ok(created);
    }

    // Cập nhật trạng thái hóa đơn theo ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateInvoiceStatus(
            @PathVariable("id") Long id,
            @RequestBody InvoiceStatusDTO invoiceStatusDTO
    ) {
        try {
            InvoiceStatusDTO updated = invoiceStatusService.updateInvoiceStatus(id, invoiceStatusDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi cập nhật trạng thái hóa đơn.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // Xóa trạng thái hóa đơn theo ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteInvoiceStatus(@PathVariable("id") Long id) {
        try {
            invoiceStatusService.deleteInvoiceStatus(id);
            return ResponseEntity.ok("Delete Successful!");
        } catch (RuntimeException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi xóa trạng thái hóa đơn.");
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
