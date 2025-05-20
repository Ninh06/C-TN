package com.example.cdtn.entity.ships;

import com.example.cdtn.entity.orders.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "shippingmethod")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "method_name", nullable = false, length = 255)
    private String methodName;

    @Column(name = "maximum_order_price_amount", nullable = false)
    private Double maximumOrderPriceAmount;

    @Column(name = "maximum_order_weight", nullable = false)
    private Double maximumOrderWeight;

    @Column(name = "minimum_order_price_amount", nullable = false)
    private Double minimumOrderPriceAmount;

    @Column(name = "minimum_order_weight", nullable = false)
    private Double minimumOrderWeight;

    @Column(name = "price_amount", nullable = false)
    private Double priceAmount;

    @OneToMany(mappedBy = "shippingMethod", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_zone_id", nullable = false)
    private ShippingZone shippingZone;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;
}
