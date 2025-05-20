package com.example.cdtn.entity.users;

import com.example.cdtn.entity.*;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.orders.ReturnOrder;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.example.cdtn.entity.shopcart.ShoppingCart;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "buyer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buyer_id")
    private Long buyerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userID", nullable = false)
    private User user;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private ShoppingCart shoppingCart;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id")
    private Wishlist wishlist;


    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ShippingAddress> shippingAddresses;

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ReturnOrder> returnOrders;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}