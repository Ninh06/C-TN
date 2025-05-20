package com.example.cdtn.entity.users;

import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long sellerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userID")
    private User user;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToOne(mappedBy = "seller",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Warehouse warehouse;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}