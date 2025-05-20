package com.example.cdtn.mapper;

import com.example.cdtn.dtos.discounts.DiscountDTO;
import com.example.cdtn.dtos.discounts.DiscountDetailDTO;
import com.example.cdtn.dtos.discounts.DiscountVoucherDTO;
import com.example.cdtn.dtos.discounts.UpdateDiscountVoucherDTO;
import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.entity.discounts.Discount;
import com.example.cdtn.entity.discounts.DiscountDetail;
import com.example.cdtn.entity.discounts.DiscountVoucher;
import com.example.cdtn.entity.orders.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiscountMapper {
    public DiscountDTO toDiscountDTO(Discount discount){
        DiscountDTO discountDTO = DiscountDTO.builder()
                .discountId(discount.getDiscountId())
                .discountName(discount.getDiscountName())
                .discountPercentage(discount.getDiscountPercentage())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .minOrderValue(discount.getMinOrderValue())
                .build();

        if(discount.getDiscountDetails()!=null && !discount.getDiscountDetails().isEmpty()) {
            List<DiscountDetailDTO> discountDetailDTOs = discount.getDiscountDetails().stream()
                    .map(discountDetail -> DiscountDetailDTO.builder()
                            .discountDetailId(discountDetail.getDiscountDetailId())
                            .discountedAmount(discountDetail.getDiscountedAmount())
                            .build())
                    .collect(Collectors.toList());
            discountDTO.setDiscountDetails(discountDetailDTOs);
        } else {
            discountDTO.setDiscountDetails(new ArrayList<>());
        }

        return discountDTO;
    }

    public Discount toDiscountEntity(DiscountDTO discountDTO) {
        if (discountDTO == null) {
            return null;
        }

        Discount discount = Discount.builder()
                .discountId(discountDTO.getDiscountId())
                .discountName(discountDTO.getDiscountName())
                .discountPercentage(discountDTO.getDiscountPercentage())
                .startDate(discountDTO.getStartDate())
                .endDate(discountDTO.getEndDate())
                .minOrderValue(discountDTO.getMinOrderValue())
                .build();

        if (discountDTO.getDiscountDetails() != null && !discountDTO.getDiscountDetails().isEmpty()) {
            List<DiscountDetail> discountDetails = discountDTO.getDiscountDetails().stream()
                    .map(discountDetailDTO -> DiscountDetail.builder()
                            .discountDetailId(discountDetailDTO.getDiscountDetailId())
                            .discountedAmount(discountDetailDTO.getDiscountedAmount())
                            .build())
                    .collect(Collectors.toList());
            discount.setDiscountDetails(discountDetails);
        } else {
            discount.setDiscountDetails(new ArrayList<>());
        }

        return discount;
    }

    public DiscountDetailDTO toDiscountDetailDTO(DiscountDetail discountDetail){
        DiscountDetailDTO discountDetailDTO = DiscountDetailDTO.builder()
                .discountDetailId(discountDetail.getDiscountDetailId())
                .discountedAmount(discountDetail.getDiscountedAmount())
                .build();

        if(discountDetail.getDiscount() != null) {
            DiscountDTO discountDTO = DiscountDTO.builder()
                    .discountId(discountDetail.getDiscount().getDiscountId())
                    .discountName(discountDetail.getDiscount().getDiscountName())
                    .discountPercentage(discountDetail.getDiscount().getDiscountPercentage())
                    .startDate(discountDetail.getDiscount().getStartDate())
                    .endDate(discountDetail.getDiscount().getEndDate())
                    .minOrderValue(discountDetail.getDiscount().getMinOrderValue())
                    .build();
            discountDetailDTO.setDiscount(discountDTO);
        }
        if(discountDetail.getOrder() != null) {
            OrderDTO orderDTO = OrderDTO.builder()
                    .orderId(discountDetail.getOrder().getOrderId())
                    .totalAmount(discountDetail.getOrder().getTotalAmount())
                    .build();
            discountDetailDTO.setOrder(orderDTO);
        }
        if(discountDetail.getVoucher() != null) {
            DiscountVoucherDTO discountVoucherDTO = DiscountVoucherDTO.builder()
                    .voucherId(discountDetail.getVoucher().getVoucherId())
                    .voucherCode(discountDetail.getVoucher().getVoucherCode())
                    .discountPercentage(discountDetail.getVoucher().getDiscountPercentage())
                    .startDate(discountDetail.getVoucher().getStartDate())
                    .endDate(discountDetail.getVoucher().getEndDate())
                    .quantityVoucher(discountDetail.getVoucher().getQuantityVoucher())
                    .oncePerCustomer(discountDetail.getVoucher().getOncePerCustomer())
                    .updatedAt(discountDetail.getVoucher().getUpdatedAt())
                    .build();
            discountDetailDTO.setDiscountVoucherDTO(discountVoucherDTO);
        }

        return discountDetailDTO;
    }

    public DiscountDetail toDiscountDetailEntity(DiscountDetailDTO discountDetailDTO) {
        if (discountDetailDTO == null) {
            return null;
        }

        DiscountDetail discountDetail = DiscountDetail.builder()
                .discountDetailId(discountDetailDTO.getDiscountDetailId())
                .discountedAmount(discountDetailDTO.getDiscountedAmount())
                .build();

        if (discountDetailDTO.getDiscount() != null) {
            Discount discount = Discount.builder()
                    .discountId(discountDetailDTO.getDiscount().getDiscountId())
                    .discountName(discountDetailDTO.getDiscount().getDiscountName())
                    .discountPercentage(discountDetailDTO.getDiscount().getDiscountPercentage())
                    .startDate(discountDetailDTO.getDiscount().getStartDate())
                    .endDate(discountDetailDTO.getDiscount().getEndDate())
                    .minOrderValue(discountDetailDTO.getDiscount().getMinOrderValue())
                    .build();
            discountDetail.setDiscount(discount);
        }

        if (discountDetailDTO.getOrder() != null) {
            Order order = Order.builder()
                    .orderId(discountDetailDTO.getOrder().getOrderId())
                    .totalAmount(discountDetailDTO.getOrder().getTotalAmount())
                    .build();
            discountDetail.setOrder(order);
        }
        if(discountDetailDTO.getDiscountVoucherDTO() != null) {
            DiscountVoucher discountVoucher = DiscountVoucher.builder()
                    .voucherId(discountDetail.getVoucher().getVoucherId())
                    .voucherCode(discountDetail.getVoucher().getVoucherCode())
                    .discountPercentage(discountDetail.getVoucher().getDiscountPercentage())
                    .startDate(discountDetail.getVoucher().getStartDate())
                    .endDate(discountDetail.getVoucher().getEndDate())
                    .quantityVoucher(discountDetail.getVoucher().getQuantityVoucher())
                    .oncePerCustomer(discountDetail.getVoucher().getOncePerCustomer())
                    .updatedAt(discountDetail.getVoucher().getUpdatedAt())
                    .build();
            discountDetail.setVoucher(discountVoucher);
        }

        return discountDetail;
    }

    public DiscountVoucherDTO toDiscountVoucherDTO(DiscountVoucher discountVoucher){
        DiscountVoucherDTO discountVoucherDTO = DiscountVoucherDTO.builder()
                .voucherId(discountVoucher.getVoucherId())
                .voucherCode(discountVoucher.getVoucherCode())
                .discountPercentage(discountVoucher.getDiscountPercentage())
                .startDate(discountVoucher.getStartDate())
                .endDate(discountVoucher.getEndDate())
                .quantityVoucher(discountVoucher.getQuantityVoucher())
                .oncePerCustomer(discountVoucher.getOncePerCustomer())
                .createdAt(discountVoucher.getCreatedAt())
                .updatedAt(discountVoucher.getUpdatedAt())
                .build();

        if(discountVoucher.getOrders() != null && !discountVoucher.getOrders().isEmpty()){
            List<OrderDTO> orderDTOs = discountVoucher.getOrders().stream()
                    .map(order -> OrderDTO.builder()
                            .orderId(order.getOrderId())
                            .totalAmount(order.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            discountVoucherDTO.setOrders(orderDTOs);
        }else {
            discountVoucherDTO.setOrders(new ArrayList<>());
        }

        if(discountVoucher.getDiscountDetails() != null && !discountVoucher.getDiscountDetails().isEmpty()){
            List<DiscountDetailDTO> discountDetailDTOs = discountVoucher.getDiscountDetails().stream()
                    .map(discountDetail -> DiscountDetailDTO.builder()
                            .discountDetailId(discountDetail.getDiscountDetailId())
                            .discountedAmount(discountDetail.getDiscountedAmount())
                            .build())
                    .collect(Collectors.toList());
            discountVoucherDTO.setDiscountDetailDTOs(discountDetailDTOs);
        }else {
            discountVoucherDTO.setDiscountDetailDTOs(new ArrayList<>());
        }

        return discountVoucherDTO;
    }

    public DiscountVoucher toDiscountVoucherEntity(DiscountVoucherDTO discountVoucherDTO) {
        if (discountVoucherDTO == null) {
            return null;
        }

        DiscountVoucher discountVoucher = DiscountVoucher.builder()
                .voucherId(discountVoucherDTO.getVoucherId())
                .voucherCode(discountVoucherDTO.getVoucherCode())
                .discountPercentage(discountVoucherDTO.getDiscountPercentage())
                .startDate(discountVoucherDTO.getStartDate())
                .endDate(discountVoucherDTO.getEndDate())
                .quantityVoucher(discountVoucherDTO.getQuantityVoucher())
                .oncePerCustomer(discountVoucherDTO.getOncePerCustomer())
                .createdAt(discountVoucherDTO.getCreatedAt())
                .updatedAt(discountVoucherDTO.getUpdatedAt())
                .build();

        if (discountVoucherDTO.getOrders() != null && !discountVoucherDTO.getOrders().isEmpty()) {
            List<Order> orders = discountVoucherDTO.getOrders().stream()
                    .map(orderDTO -> Order.builder()
                            .orderId(orderDTO.getOrderId())
                            .totalAmount(orderDTO.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            discountVoucher.setOrders(orders);
        } else {
            discountVoucher.setOrders(new ArrayList<>());
        }

        if(discountVoucherDTO.getDiscountDetailDTOs() != null && !discountVoucherDTO.getDiscountDetailDTOs().isEmpty()){
            List<DiscountDetail> discountDetails = discountVoucherDTO.getDiscountDetailDTOs().stream()
                    .map(discountDetailDTO -> DiscountDetail.builder()
                            .discountDetailId(discountDetailDTO.getDiscountDetailId())
                            .discountedAmount(discountDetailDTO.getDiscountedAmount())
                            .build())
                    .collect(Collectors.toList());
            discountVoucher.setDiscountDetails(discountDetails);
        }else {
            discountVoucher.setDiscountDetails(new ArrayList<>());
        }

        return discountVoucher;
    }

    public void updateDiscountVoucherFromDTO(UpdateDiscountVoucherDTO dto, DiscountVoucher entity) {
        if (dto.getVoucherCode() != null) {
            entity.setVoucherCode(dto.getVoucherCode());
        }
        if (dto.getDiscountPercentage() != null) {
            entity.setDiscountPercentage(dto.getDiscountPercentage());
        }
        if (dto.getStartDate() != null) {
            entity.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            entity.setEndDate(dto.getEndDate());
        }
        if (dto.getQuantityVoucher() != 0) {
            entity.setQuantityVoucher(dto.getQuantityVoucher());
        }
        if (dto.getOncePerCustomer() != null) {
            entity.setOncePerCustomer(dto.getOncePerCustomer());
        }
    }



}
