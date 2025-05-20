package com.example.cdtn.mapper;

import com.example.cdtn.dtos.invoices.InvoiceDTO;
import com.example.cdtn.dtos.invoices.InvoiceStatusDTO;
import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.entity.invoices.Invoice;
import com.example.cdtn.entity.invoices.InvoiceStatus;
import com.example.cdtn.entity.orders.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    public InvoiceStatusDTO toInvoiceStatusDTO(InvoiceStatus invoiceStatus) {
        if(invoiceStatus == null) {
            return null;
        }

        InvoiceStatusDTO invoiceStatusDTO = InvoiceStatusDTO.builder()
                .invoiceStatusId(invoiceStatus.getInvoiceStatusId())
                .invoiceStatusDesc(invoiceStatus.getInvoiceStatusDesc())
                .build();
        if(invoiceStatus.getInvoices() != null && !invoiceStatus.getInvoices().isEmpty()) {
            List<InvoiceDTO> invoiceDTOs = invoiceStatus.getInvoices().stream()
                    .map(invoice -> InvoiceDTO.builder()
                            .invoiceId(invoice.getInvoiceId())
                            .totalAmount(invoice.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            invoiceStatusDTO.setInvoices(invoiceDTOs);
        } else {
            invoiceStatusDTO.setInvoices(new ArrayList<>());
        }
        return invoiceStatusDTO;
    }

    public InvoiceStatus toInvoiceStatusEntity(InvoiceStatusDTO invoiceStatusDTO) {
        if (invoiceStatusDTO == null) {
            return null;
        }

        // Tạo đối tượng InvoiceStatus từ DTO
        InvoiceStatus invoiceStatus = InvoiceStatus.builder()
                .invoiceStatusId(invoiceStatusDTO.getInvoiceStatusId())
                .invoiceStatusDesc(invoiceStatusDTO.getInvoiceStatusDesc())
                .build();

        // Nếu có danh sách invoices trong DTO, chuyển đổi sang danh sách Invoice entities
        if (invoiceStatusDTO.getInvoices() != null && !invoiceStatusDTO.getInvoices().isEmpty()) {
            List<Invoice> invoices = invoiceStatusDTO.getInvoices().stream()
                    .map(invoiceDTO -> Invoice.builder()
                            .invoiceId(invoiceDTO.getInvoiceId())
                            .totalAmount(invoiceDTO.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            invoiceStatus.setInvoices(invoices);
        } else {
            invoiceStatus.setInvoices(new ArrayList<>());
        }

        return invoiceStatus;
    }

    public InvoiceDTO toInvoiceDTO(Invoice invoice) {
        if(invoice == null) {
            return null;
        }
        InvoiceDTO invoiceDTO = InvoiceDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .totalAmount(invoice.getTotalAmount())
                .build();
        if(invoice.getOrder() != null) {
            OrderDTO orderDTO = OrderDTO.builder()
                    .orderId(invoice.getOrder().getOrderId())
                    .totalAmount(invoice.getOrder().getTotalAmount())
                    .build();
            invoiceDTO.setOrder(orderDTO);
        }
        if(invoice.getInvoiceStatus() != null) {
            InvoiceStatusDTO invoiceStatusDTO = InvoiceStatusDTO.builder()
                    .invoiceStatusId(invoice.getInvoiceStatus().getInvoiceStatusId())
                    .invoiceStatusDesc(invoice.getInvoiceStatus().getInvoiceStatusDesc())
                    .build();
            invoiceDTO.setInvoiceStatus(invoiceStatusDTO);
        }

        return invoiceDTO;
    }

    public Invoice toInvoiceEntity(InvoiceDTO invoiceDTO) {
        if (invoiceDTO == null) {
            return null;
        }

        Invoice invoice = Invoice.builder()
                .invoiceId(invoiceDTO.getInvoiceId())
                .totalAmount(invoiceDTO.getTotalAmount())
                .build();

        // Chuyển OrderDTO về Order entity nếu tồn tại
        if (invoiceDTO.getOrder() != null) {
            Order order = Order.builder()
                    .orderId(invoiceDTO.getOrder().getOrderId())
                    .totalAmount(invoiceDTO.getOrder().getTotalAmount())
                    .build();
            invoice.setOrder(order);
        }

        // Chuyển InvoiceStatusDTO về InvoiceStatus entity nếu tồn tại
        if (invoiceDTO.getInvoiceStatus() != null) {
            InvoiceStatus invoiceStatus = InvoiceStatus.builder()
                    .invoiceStatusId(invoiceDTO.getInvoiceStatus().getInvoiceStatusId())
                    .invoiceStatusDesc(invoiceDTO.getInvoiceStatus().getInvoiceStatusDesc())
                    .build();
            invoice.setInvoiceStatus(invoiceStatus);
        }

        return invoice;
    }


}
