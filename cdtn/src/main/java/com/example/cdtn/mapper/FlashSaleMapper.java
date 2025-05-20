package com.example.cdtn.mapper;

import com.example.cdtn.dtos.flashsale.FlashSaleDTO;
import com.example.cdtn.dtos.flashsale.ProductFlashSaleDTO;
import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.entity.flashsale.FlashSale;
import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.products.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlashSaleMapper {

    public ProductFlashSaleDTO toProductFlashSaleDTO(ProductFlashSale productFlashSale) {
        if (productFlashSale == null) {
            return null;
        }
        ProductFlashSaleDTO productFlashSaleDTO = ProductFlashSaleDTO.builder()
                .id(productFlashSale.getId())
                .flashSalePrice(productFlashSale.getFlashSalePrice())
                .originalPrice(productFlashSale.getOriginalPrice())
                .discountPercentage(productFlashSale.getDiscountPercentage())
                .quota(productFlashSale.getQuota())
                .soldCount(productFlashSale.getSoldCount())
                .isActive(productFlashSale.getIsActive())
                .build();


        if(productFlashSale.getProduct() != null) {
            ProductDTO productDTO = ProductDTO.builder()
                    .productId(productFlashSale.getProduct().getProductId())
                    .name(productFlashSale.getProduct().getName())
                    .title(productFlashSale.getProduct().getTitle())
                    .description(productFlashSale.getProduct().getDescription())
                    .weight(productFlashSale.getProduct().getWeight())
                    .currency(productFlashSale.getProduct().getCurrency())
                    .minimalVariantPriceAmount(productFlashSale.getProduct().getMinimalVariantPriceAmount())
                    .minimalQuantity(productFlashSale.getProduct().getMinimalQuantity())
                    .availableForPurchase(productFlashSale.getProduct().getAvailableForPurchase())
                    .build();
            productFlashSaleDTO.setProduct(productDTO);
        }

        if(productFlashSale.getFlashSale() != null) {
            FlashSaleDTO flashSaleDTO = FlashSaleDTO.builder()
                    .flashSaleId(productFlashSale.getFlashSale().getFlashSaleId())
                    .name(productFlashSale.getFlashSale().getName())
                    .description(productFlashSale.getFlashSale().getDescription())
                    .startTime(productFlashSale.getFlashSale().getStartTime())
                    .endTime(productFlashSale.getFlashSale().getEndTime())
                    .status(productFlashSale.getFlashSale().getStatus())
                    .isActive(productFlashSale.getFlashSale().getIsActive())
                    .build();
            productFlashSaleDTO.setFlashSale(flashSaleDTO);
        }
        return productFlashSaleDTO;
    }

    public ProductFlashSale toProductFlashSaleEntity(ProductFlashSaleDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductFlashSale productFlashSale = ProductFlashSale.builder()
                .id(dto.getId())
                .flashSalePrice(dto.getFlashSalePrice())
                .originalPrice(dto.getOriginalPrice())
                .discountPercentage(dto.getDiscountPercentage())
                .quota(dto.getQuota())
                .soldCount(dto.getSoldCount())
                .isActive(dto.getIsActive())
                .build();

        if (dto.getProduct() != null) {
            Product product = Product.builder()
                    .productId(dto.getProduct().getProductId())
                    .name(dto.getProduct().getName())
                    .title(dto.getProduct().getTitle())
                    .description(dto.getProduct().getDescription())
                    .weight(dto.getProduct().getWeight())
                    .currency(dto.getProduct().getCurrency())
                    .minimalVariantPriceAmount(dto.getProduct().getMinimalVariantPriceAmount())
                    .minimalQuantity(dto.getProduct().getMinimalQuantity())
                    .availableForPurchase(dto.getProduct().getAvailableForPurchase())
                    .build();
            productFlashSale.setProduct(product);
        }

        if (dto.getFlashSale() != null) {
            FlashSale flashSale = FlashSale.builder()
                    .flashSaleId(dto.getFlashSale().getFlashSaleId())
                    .name(dto.getFlashSale().getName())
                    .description(dto.getFlashSale().getDescription())
                    .startTime(dto.getFlashSale().getStartTime())
                    .endTime(dto.getFlashSale().getEndTime())
                    .status(dto.getFlashSale().getStatus())
                    .isActive(dto.getFlashSale().getIsActive())
                    .build();
            productFlashSale.setFlashSale(flashSale);
        }

        return productFlashSale;
    }


    public FlashSaleDTO  toFlashSaleDTO(FlashSale flashSale) {
        if (flashSale == null) {
            return null;
        }

        FlashSaleDTO flashSaleDTO = FlashSaleDTO.builder()
                .flashSaleId(flashSale.getFlashSaleId())
                .name(flashSale.getName())
                .description(flashSale.getDescription())
                .startTime(flashSale.getStartTime())
                .endTime(flashSale.getEndTime())
                .status(flashSale.getStatus())
                .isActive(flashSale.getIsActive())
                .build();

        if(flashSale.getProductFlashSales() != null && !flashSale.getProductFlashSales().isEmpty()) {
            List<ProductFlashSaleDTO> productFlashSaleDTOs = flashSale.getProductFlashSales().stream()
                    .map(productFlashSale -> ProductFlashSaleDTO.builder()
                            .id(productFlashSale.getId())
                            .flashSalePrice(productFlashSale.getFlashSalePrice())
                            .originalPrice(productFlashSale.getOriginalPrice())
                            .discountPercentage(productFlashSale.getDiscountPercentage())
                            .quota(productFlashSale.getQuota())
                            .soldCount(productFlashSale.getSoldCount())
                            .build())
                    .collect(Collectors.toList());
            flashSaleDTO.setProductFlashSales(productFlashSaleDTOs);
        }
        return flashSaleDTO;
    }

    public FlashSale toFlashSaleEntity(FlashSaleDTO dto) {
        if (dto == null) {
            return null;
        }

        FlashSale flashSale = FlashSale.builder()
                .flashSaleId(dto.getFlashSaleId())
                .name(dto.getName())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(dto.getStatus())
                .isActive(dto.getIsActive())
                .build();

        if (dto.getProductFlashSales() != null && !dto.getProductFlashSales().isEmpty()) {
            List<ProductFlashSale> productFlashSales = dto.getProductFlashSales().stream()
                    .map(productFlashSaleDTO -> ProductFlashSale.builder()
                            .id(productFlashSaleDTO.getId())
                            .flashSalePrice(productFlashSaleDTO.getFlashSalePrice())
                            .originalPrice(productFlashSaleDTO.getOriginalPrice())
                            .discountPercentage(productFlashSaleDTO.getDiscountPercentage())
                            .quota(productFlashSaleDTO.getQuota())
                            .soldCount(productFlashSaleDTO.getSoldCount())
                            .build())
                    .collect(Collectors.toList());
            flashSale.setProductFlashSales(productFlashSales);
        }

        return flashSale;
    }

}
