package com.example.cdtn.entity.flashsale;

import com.example.cdtn.entity.products.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product_flash_sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;

    @Column(name = "flash_sale_price", nullable = false)
    private Double flashSalePrice;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "quota", nullable = false)
    private Integer quota;

    @Column(name = "sold_count")
    private Integer soldCount;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "productFlashSale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariantFlashSale> productVariantFlashSales;
}
