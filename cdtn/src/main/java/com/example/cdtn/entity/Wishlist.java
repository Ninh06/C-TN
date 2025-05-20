package com.example.cdtn.entity;

import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.users.Buyer;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long wishlistId;

    @OneToOne(mappedBy = "wishlist", fetch = FetchType.LAZY)
    private Buyer buyer;

    @ManyToMany
    @JoinTable(
            name = "wishlist_product", // bảng trung gian
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

}