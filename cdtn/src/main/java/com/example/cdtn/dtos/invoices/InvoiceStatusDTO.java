package com.example.cdtn.dtos.invoices;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceStatusDTO {

    private Long invoiceStatusId;

    @NotBlank(message = "Mô tả trạng thái hóa đơn không được để trống")
    private String invoiceStatusDesc;

    private Date createdAt;

    private List<InvoiceDTO> invoices;
}
