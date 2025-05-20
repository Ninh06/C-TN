package com.example.cdtn.entity.flashsale;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "flash_sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flash_sale_id")
    private Long flashSaleId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @Column(name = "status")
    private String status;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "flashSale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductFlashSale> productFlashSales;
}
