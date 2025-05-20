package com.example.cdtn.mapper;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.ships.ShippingAddressDTO;
import com.example.cdtn.dtos.ships.ShippingMethodDTO;
import com.example.cdtn.dtos.ships.ShippingZoneDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.example.cdtn.entity.ships.ShippingMethod;
import com.example.cdtn.entity.ships.ShippingZone;
import com.example.cdtn.entity.users.Buyer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShippingMapper {
    public ShippingAddressDTO toShippingAddressDTO(ShippingAddress shippingAddress) {
        if(shippingAddress == null) {
            return null;
        }

        ShippingAddressDTO shippingAddressDTO = ShippingAddressDTO.builder()
                .id(shippingAddress.getId())
                .firstName(shippingAddress.getFirstName())
                .lastName(shippingAddress.getLastName())
                .personName(shippingAddress.getPersonName())
                .streetAddress1(shippingAddress.getStreetAddress1())
                .streetAddress2(shippingAddress.getStreetAddress2())
                .city(shippingAddress.getCity())
                .cityArea(shippingAddress.getCityArea())
                .postalCode(shippingAddress.getPostalCode())
                .country(shippingAddress.getCountry())
                .countryArea(shippingAddress.getCountryArea())
                .phone(shippingAddress.getPhone())
                .build();

        if(shippingAddress.getBuyer() != null) {
            BuyerDTO buyerDTO = BuyerDTO.builder()
                    .buyerId(shippingAddress.getBuyer().getBuyerId())
                    .build();
            shippingAddressDTO.setBuyer(buyerDTO);
        }
        if (shippingAddress.getOrders() != null && !shippingAddress.getOrders().isEmpty()) {
            List<OrderDTO> orderDTOs = shippingAddress.getOrders().stream()
                    .map(order -> OrderDTO.builder()
                            .orderId(order.getOrderId())
                            .totalAmount(order.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            shippingAddressDTO.setOrders(orderDTOs);
        } else {
            shippingAddressDTO.setOrders(new ArrayList<>());
        }

        return shippingAddressDTO;
    }

    public ShippingAddress toShippingAddressEntity(ShippingAddressDTO shippingAddressDTO) {
        if (shippingAddressDTO == null) {
            return null;
        }

        ShippingAddress shippingAddress = ShippingAddress.builder()
                .id(shippingAddressDTO.getId())
                .firstName(shippingAddressDTO.getFirstName())
                .lastName(shippingAddressDTO.getLastName())
                .personName(shippingAddressDTO.getPersonName())
                .streetAddress1(shippingAddressDTO.getStreetAddress1())
                .streetAddress2(shippingAddressDTO.getStreetAddress2())
                .city(shippingAddressDTO.getCity())
                .cityArea(shippingAddressDTO.getCityArea())
                .postalCode(shippingAddressDTO.getPostalCode())
                .country(shippingAddressDTO.getCountry())
                .countryArea(shippingAddressDTO.getCountryArea())
                .phone(shippingAddressDTO.getPhone())
                .build();

        if (shippingAddressDTO.getBuyer() != null) {
            Buyer buyer = Buyer.builder()
                    .buyerId(shippingAddressDTO.getBuyer().getBuyerId())
                    .build();
            shippingAddress.setBuyer(buyer);
        }

        if (shippingAddressDTO.getOrders() != null && !shippingAddressDTO.getOrders().isEmpty()) {
            List<Order> orders = shippingAddressDTO.getOrders().stream()
                    .map(orderDTO -> Order.builder()
                            .orderId(orderDTO.getOrderId())
                            .totalAmount(orderDTO.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            shippingAddress.setOrders(orders);
        } else {
            shippingAddress.setOrders(new ArrayList<>());
        }

        return shippingAddress;
    }

    public ShippingMethodDTO toShippingMethodDTO(ShippingMethod shippingMethod) {
        if(shippingMethod == null) {
            return null;
        }

        ShippingMethodDTO shippingMethodDTO = ShippingMethodDTO.builder()
                .id(shippingMethod.getId())
                .methodName(shippingMethod.getMethodName())
                .maximumOrderWeight(shippingMethod.getMaximumOrderWeight())
                .maximumOrderPriceAmount(shippingMethod.getMaximumOrderPriceAmount())
                .minimumOrderPriceAmount(shippingMethod.getMinimumOrderPriceAmount())
                .minimumOrderWeight(shippingMethod.getMinimumOrderWeight())
                .priceAmount(shippingMethod.getPriceAmount())
                .currency(shippingMethod.getCurrency())
                .isDefault(shippingMethod.getIsDefault())
                .build();
        if (shippingMethod.getOrders() != null && !shippingMethod.getOrders().isEmpty()) {
            List<OrderDTO> orderDTOs = shippingMethod.getOrders().stream()
                    .map(order -> OrderDTO.builder()
                            .orderId(order.getOrderId())
                            .totalAmount(order.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            shippingMethodDTO.setOrders(orderDTOs);
        } else {
            shippingMethodDTO.setOrders(new ArrayList<>());
        }

        if(shippingMethod.getShippingZone() != null) {
            ShippingZoneDTO shippingZoneDTO = ShippingZoneDTO.builder()
                    .id(shippingMethod.getShippingZone().getId())
                    .zoneName(shippingMethod.getShippingZone().getZoneName())
                    .description(shippingMethod.getShippingZone().getDescription())
                    .build();
            shippingMethodDTO.setShippingZoneId(shippingZoneDTO);
        }

        return shippingMethodDTO;
    }

    public ShippingMethod toShippingMethodEntity(ShippingMethodDTO shippingMethodDTO) {
        if (shippingMethodDTO == null) {
            return null;
        }

        ShippingMethod shippingMethod = ShippingMethod.builder()
                .id(shippingMethodDTO.getId())
                .methodName(shippingMethodDTO.getMethodName())
                .maximumOrderWeight(shippingMethodDTO.getMaximumOrderWeight())
                .maximumOrderPriceAmount(shippingMethodDTO.getMaximumOrderPriceAmount())
                .minimumOrderPriceAmount(shippingMethodDTO.getMinimumOrderPriceAmount())
                .minimumOrderWeight(shippingMethodDTO.getMinimumOrderWeight())
                .priceAmount(shippingMethodDTO.getPriceAmount())
                .currency(shippingMethodDTO.getCurrency())
                .isDefault(shippingMethodDTO.getIsDefault())
                .build();

        if (shippingMethodDTO.getOrders() != null && !shippingMethodDTO.getOrders().isEmpty()) {
            List<Order> orders = shippingMethodDTO.getOrders().stream()
                    .map(orderDTO -> Order.builder()
                            .orderId(orderDTO.getOrderId())
                            .totalAmount(orderDTO.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            shippingMethod.setOrders(orders);
        } else {
            shippingMethod.setOrders(new ArrayList<>());
        }

        if (shippingMethodDTO.getShippingZoneId() != null) {
            ShippingZone shippingZone = ShippingZone.builder()
                    .id(shippingMethodDTO.getShippingZoneId().getId())
                    .zoneName(shippingMethodDTO.getShippingZoneId().getZoneName())
                    .description(shippingMethodDTO.getShippingZoneId().getDescription())
                    .build();
            shippingMethod.setShippingZone(shippingZone);
        }

        return shippingMethod;
    }

    public ShippingZoneDTO toShippingZoneDTO(ShippingZone shippingZone) {
        if(shippingZone == null) {
            return null;
        }

        ShippingZoneDTO shippingZoneDTO = ShippingZoneDTO.builder()
                .id(shippingZone.getId())
                .zoneName(shippingZone.getZoneName())
                .description(shippingZone.getDescription())
                .build();

        if (shippingZone.getShippingMethods() != null && !shippingZone.getShippingMethods().isEmpty()) {
            List<ShippingMethodDTO> shippingMethodDTOs = shippingZone.getShippingMethods().stream()
                    .map(shippingMethod -> ShippingMethodDTO.builder()
                            .id(shippingMethod.getId())
                            .methodName(shippingMethod.getMethodName())
                            .maximumOrderPriceAmount(shippingMethod.getMaximumOrderPriceAmount())
                            .maximumOrderWeight(shippingMethod.getMaximumOrderWeight())
                            .minimumOrderPriceAmount(shippingMethod.getMinimumOrderPriceAmount())
                            .minimumOrderWeight(shippingMethod.getMinimumOrderWeight())
                            .priceAmount(shippingMethod.getPriceAmount())
                            .currency(shippingMethod.getCurrency())
                            .isDefault(shippingMethod.getIsDefault())
                            .build())
                    .collect(Collectors.toList());
            shippingZoneDTO.setShippingMethods(shippingMethodDTOs);
        } else {
            shippingZoneDTO.setShippingMethods(new ArrayList<>());
        }
        return shippingZoneDTO;
    }

    public ShippingZone toShippingZoneEntity(ShippingZoneDTO shippingZoneDTO) {
        if (shippingZoneDTO == null) {
            return null;
        }

        ShippingZone shippingZone = ShippingZone.builder()
                .id(shippingZoneDTO.getId())
                .zoneName(shippingZoneDTO.getZoneName())
                .description(shippingZoneDTO.getDescription())
                .build();

        if (shippingZoneDTO.getShippingMethods() != null && !shippingZoneDTO.getShippingMethods().isEmpty()) {
            List<ShippingMethod> shippingMethods = shippingZoneDTO.getShippingMethods().stream()
                    .map(shippingMethodDTO -> ShippingMethod.builder()
                            .id(shippingMethodDTO.getId())
                            .methodName(shippingMethodDTO.getMethodName())
                            .maximumOrderPriceAmount(shippingMethodDTO.getMaximumOrderPriceAmount())
                            .maximumOrderWeight(shippingMethodDTO.getMaximumOrderWeight())
                            .minimumOrderPriceAmount(shippingMethodDTO.getMinimumOrderPriceAmount())
                            .minimumOrderWeight(shippingMethodDTO.getMinimumOrderWeight())
                            .priceAmount(shippingMethodDTO.getPriceAmount())
                            .currency(shippingMethodDTO.getCurrency())
                            .isDefault(shippingMethodDTO.getIsDefault())
                            .build())
                    .collect(Collectors.toList());
            shippingZone.setShippingMethods(shippingMethods);
        } else {
            shippingZone.setShippingMethods(new ArrayList<>());
        }

        return shippingZone;
    }

}
