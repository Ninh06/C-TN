package com.example.cdtn.entity;

import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.users.Buyer;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "payment_description", nullable = false, length = 150)
    private String paymentDes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY)
    private List<Order> orders;


    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}