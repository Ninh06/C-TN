package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.ships.ShippingZoneDTO;
import com.example.cdtn.entity.ships.ShippingZone;
import com.example.cdtn.mapper.ShippingMapper;
import com.example.cdtn.repository.ShippingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ShippingZoneService {
    @Autowired
    private ShippingZoneRepository shippingZoneRepository;
    @Autowired
    private ShippingMapper shippingMapper;

    /** Lấy tất cả vùng giao hàng*/
    public List<ShippingZoneDTO> getAllShippingZones() {
        return shippingZoneRepository.findAll()
                .stream()
                .map(shippingMapper::toShippingZoneDTO)
                .collect(Collectors.toList());
    }

    /** Lấy vùng giao hàng theo ID*/
    public ShippingZoneDTO getShippingZoneById(Long id) {
        ShippingZone zone = shippingZoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vùng vận chuyển với id: " + id));
        return shippingMapper.toShippingZoneDTO(zone);
    }

    /** Tạo mới vùng giao hàng*/
    public ShippingZoneDTO createShippingZone(ShippingZoneDTO dto) {
        ShippingZone zone = shippingMapper.toShippingZoneEntity(dto);
        ShippingZone saved = shippingZoneRepository.save(zone);
        return shippingMapper.toShippingZoneDTO(saved);
    }

    /** Cập nhật vùng giao hàng*/
    public ShippingZoneDTO updateShippingZone(Long id, ShippingZoneDTO dto) {
        ShippingZone existing = shippingZoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vùng vận chuyển với id: " + id));

        // Cập nhật zoneName nếu có giá trị
        if (dto.getZoneName() != null) {
            existing.setZoneName(dto.getZoneName());
        }

        // Cập nhật description nếu có giá trị
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        // Cập nhật shipping methods nếu có
        if (dto.getShippingMethods() != null) {
            existing.setShippingMethods(
                    dto.getShippingMethods().stream()
                            .map(shippingMapper::toShippingMethodEntity)
                            .collect(Collectors.toList())
            );
        }

        ShippingZone updated = shippingZoneRepository.save(existing);
        return shippingMapper.toShippingZoneDTO(updated);
    }


    /** Xoá vùng giao hàng*/
    public void deleteShippingZone(Long id) {
        if (!shippingZoneRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy vùng vận chuyển với id: " + id);
        }
        shippingZoneRepository.deleteById(id);
    }
}
