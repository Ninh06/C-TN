package com.example.cdtn.entity.shopcart;

import com.example.cdtn.entity.shopcart.CartItem;
import com.example.cdtn.entity.users.Buyer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @OneToOne(mappedBy = "shoppingCart", fetch = FetchType.LAZY)
    private Buyer buyer;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

}