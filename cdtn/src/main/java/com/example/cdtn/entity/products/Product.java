package com.example.cdtn.entity.products;

import com.example.cdtn.entity.*;
import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.orders.OrderItem;
import com.example.cdtn.entity.orders.ReturnOrder;
import com.example.cdtn.entity.shopcart.CartItem;
import com.example.cdtn.entity.users.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "currency")
    private String currency;

    @Column(name = "minimal_variant_price_amount")
    private Double minimalVariantPriceAmount;

    @Column(name = "minimal_quantity")
    private Long minimalQuantity;

    @Column(name = "available_for_purchase")
    private Boolean availableForPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", referencedColumnName = "id", nullable = false)
    private ProductType productType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductVariant> productVariants;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductFlashSale> productFlashSales;


}