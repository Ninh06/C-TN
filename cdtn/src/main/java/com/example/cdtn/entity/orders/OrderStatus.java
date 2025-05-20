package com.example.cdtn.entity.orders;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orderstatus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderstatus_id")
    private Long orderStatusId;

    @Column(name = "orderstatus_desc", nullable = false, length = 255)
    private String orderStatusDesc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    // Quan hệ 1-n với Order
    @OneToMany(mappedBy = "orderStatus", fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "orderStatus", fetch = FetchType.LAZY)
    private List<ReturnOrder> returnOrders;


    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

}