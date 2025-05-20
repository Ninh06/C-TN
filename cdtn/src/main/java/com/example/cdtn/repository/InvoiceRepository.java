package com.example.cdtn.repository;

import com.example.cdtn.entity.invoices.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByOrderOrderId(Long orderId);
}
