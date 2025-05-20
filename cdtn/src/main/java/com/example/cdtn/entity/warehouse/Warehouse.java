package com.example.cdtn.entity.warehouse;

import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.users.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String nameWarehouse;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false, unique = true)
    private Address address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "seller_id", nullable = false, unique = true)
    private Seller seller;

}
