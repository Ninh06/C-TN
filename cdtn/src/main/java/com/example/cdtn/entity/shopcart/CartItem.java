package com.example.cdtn.entity.shopcart;

import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price_item", nullable = false)
    private BigDecimal totalPriceItem;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

//    @Column(name = "unit_weight", nullable = false)
//    private Double unitWeight;  // Đơn giá của sản phẩm trong giỏ hàng
//
//    @Column(name = "total_weight", nullable = false)
//    private Double totalWeight;  // Đơn giá của sản phẩm trong giỏ hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}