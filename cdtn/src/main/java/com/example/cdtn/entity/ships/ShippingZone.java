package com.example.cdtn.entity.ships;

import com.example.cdtn.entity.ships.ShippingMethod;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "shipping_zone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "zone_name", nullable = false, length = 255, unique = true)
    private String zoneName;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "shippingZone", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingMethod> shippingMethods;

}

