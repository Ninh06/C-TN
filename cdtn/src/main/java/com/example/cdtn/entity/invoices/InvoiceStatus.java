package com.example.cdtn.entity.invoices;

import com.example.cdtn.entity.invoices.Invoice;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "invoice_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_status_id")
    private Long invoiceStatusId;

    @Column(name = "invoice_status_desc", nullable = false, length = 255)
    private String invoiceStatusDesc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "invoiceStatus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}