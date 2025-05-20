package com.example.cdtn.entity.discounts;

import com.example.cdtn.entity.orders.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "discount_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_detail_id")
    private Long discountDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private DiscountVoucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "discounted_amount", nullable = false)
    private Double discountedAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}