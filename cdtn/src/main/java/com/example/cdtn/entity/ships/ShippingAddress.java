package com.example.cdtn.entity.ships;

import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.users.Buyer;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "shipping_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "person_name", length = 100)
    private String personName;

    @Column(name = "street_address_1", length = 255)
    private String streetAddress1;

    @Column(name = "street_address_2", length = 255)
    private String streetAddress2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "country_area", length = 100)
    private String countryArea;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "city_area", length = 100)
    private String cityArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @OneToMany(mappedBy = "shippingAddress", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
}
