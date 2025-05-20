package com.example.cdtn.repository;

import com.example.cdtn.entity.invoices.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Long> {
    Optional<InvoiceStatus> findByInvoiceStatusDesc(String invoiceStatusDesc);
}
