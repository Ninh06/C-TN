package com.example.cdtn.entity;


import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    private Seller seller;

    // Quan hệ 1-1 với Warehouse
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    private Warehouse warehouse;

}

