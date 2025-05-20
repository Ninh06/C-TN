package com.example.cdtn.entity.products;

import com.example.cdtn.entity.products.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product_producttype")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "productType",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Product> products;
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}