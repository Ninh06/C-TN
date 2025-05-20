package com.example.cdtn.entity.discounts;

import com.example.cdtn.entity.orders.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "discount_voucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "voucher_code", nullable = false, unique = true, length = 50)
    private String voucherCode;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "quantity_voucher", nullable = false)
    private int quantityVoucher;

    @Column(name = "once_per_customer", nullable = false)
    private Boolean oncePerCustomer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiscountDetail> discountDetails;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdated() {
        this.updatedAt = new Date();
    }

}