package com.example.cdtn.entity.discounts;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "discount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long discountId;

    @Column(name = "discount_name", nullable = false, length = 255)
    private String discountName;

    @Column(name = "discount_percentage", nullable = false)
    private BigDecimal discountPercentage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "min_order_value")
    private BigDecimal minOrderValue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountDetail> discountDetails;
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

}