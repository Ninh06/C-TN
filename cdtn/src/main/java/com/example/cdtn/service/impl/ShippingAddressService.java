package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.ships.ShippingAddressDTO;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.mapper.ShippingMapper;
import com.example.cdtn.repository.BuyerRepository;
import com.example.cdtn.repository.ShippingAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;
    @Autowired
    private ShippingMapper shippingMapper;

    /** Thêm địa chỉ giao hàng cho Buyer*/
    public ShippingAddressDTO addShippingAddress(Long buyerId, ShippingAddressDTO dto) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người mua có id: " + buyerId));

        ShippingAddress entity = shippingMapper.toShippingAddressEntity(dto);
        entity.setBuyer(buyer);

        ShippingAddress saved = shippingAddressRepository.save(entity);
        return shippingMapper.toShippingAddressDTO(saved);
    }

    /** Lấy tất cả địa chỉ giao hàng của Buyer*/
    public List<ShippingAddressDTO> getShippingAddressesByBuyer(Long buyerId) {
        buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người mua có id: " + buyerId));

        return shippingAddressRepository.findByBuyerBuyerId(buyerId).stream()
                .map(shippingMapper::toShippingAddressDTO)
                .collect(Collectors.toList());
    }

    /** Xoá địa chỉ giao hàng theo ID và buyerId*/
    public void deleteShippingAddress(Long buyerId, Long addressId) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));

        if (!address.getBuyer().getBuyerId().equals(buyerId)) {
            throw new RuntimeException("Địa chỉ không thuộc về người mua");
        }

        shippingAddressRepository.delete(address);
    }

    /** Cập nhật địa chỉ giao hàng theo buyerId*/
    public ShippingAddressDTO updateShippingAddress(Long buyerId, Long addressId, ShippingAddressDTO dto) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));

        if (!address.getBuyer().getBuyerId().equals(buyerId)) {
            throw new RuntimeException("Địa chỉ không thuộc về người mua");
        }

        if (dto.getFirstName() != null) address.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) address.setLastName(dto.getLastName());
        if (dto.getPersonName() != null) address.setPersonName(dto.getPersonName());
        if (dto.getStreetAddress1() != null) address.setStreetAddress1(dto.getStreetAddress1());
        if (dto.getStreetAddress2() != null) address.setStreetAddress2(dto.getStreetAddress2());
        if (dto.getCity() != null) address.setCity(dto.getCity());
        if (dto.getCityArea() != null) address.setCityArea(dto.getCityArea());
        if (dto.getPostalCode() != null) address.setPostalCode(dto.getPostalCode());
        if (dto.getCountry() != null) address.setCountry(dto.getCountry());
        if (dto.getCountryArea() != null) address.setCountryArea(dto.getCountryArea());
        if (dto.getPhone() != null) address.setPhone(dto.getPhone());

        ShippingAddress updated = shippingAddressRepository.save(address);
        return shippingMapper.toShippingAddressDTO(updated);
    }

    public ShippingAddress getShippingAddressById(Long id) {
        return shippingAddressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ giao hàng với ID: " + id));
    }

}
