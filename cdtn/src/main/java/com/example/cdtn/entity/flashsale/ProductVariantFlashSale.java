package com.example.cdtn.entity.flashsale;

import com.example.cdtn.entity.products.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant_flash_sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantFlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_flash_sale_id")
    private ProductFlashSale productFlashSale;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "flash_sale_price")
    private Double flashSalePrice;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "is_active")
    private Boolean isActive;
}
