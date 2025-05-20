package com.example.cdtn.dtos.invoices;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDTO {

    private Long invoiceId;

    private OrderDTO order;

    private InvoiceStatusDTO invoiceStatus;

    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tổng tiền phải lớn hơn hoặc bằng 0")
    private BigDecimal totalAmount;

    private String transactionId;

    private Date createdAt;
}
