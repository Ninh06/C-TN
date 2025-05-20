package com.example.cdtn.entity.orders;


import com.example.cdtn.entity.invoices.Invoice;
import com.example.cdtn.entity.Payment;
import com.example.cdtn.entity.discounts.DiscountDetail;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.example.cdtn.entity.ships.ShippingMethod;
import com.example.cdtn.entity.discounts.DiscountVoucher;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.entity.users.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "e_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private DiscountVoucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shippingmethod_id")
    private ShippingMethod shippingMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shippingaddress_id", referencedColumnName = "id")
    private ShippingAddress shippingAddress;

    // Một đơn hàng có thể chứa nhiều OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<DiscountDetail> discountDetails;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Invoice invoice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ReturnOrder returnOrder;


    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}