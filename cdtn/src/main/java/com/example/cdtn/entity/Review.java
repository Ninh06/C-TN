package com.example.cdtn.entity;

import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.users.Buyer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @Column(name = "rating", nullable = false)
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    private Integer rating;

    @Column(name = "review_text", length = 1000)
    private String reviewText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}