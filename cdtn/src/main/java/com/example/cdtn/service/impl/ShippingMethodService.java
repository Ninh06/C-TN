package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.ships.ShippingMethodDTO;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.ships.ShippingMethod;
import com.example.cdtn.entity.ships.ShippingZone;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.ShippingMapper;
import com.example.cdtn.repository.ShippingMethodRepository;
import com.example.cdtn.repository.ShippingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingMethodService {
    @Autowired
    private ShippingMethodRepository shippingMethodRepository;
    @Autowired
    private ShippingZoneRepository shippingZoneRepository;
    @Autowired
    private ShippingMapper shippingMapper;

    public List<ShippingMethodDTO> getAllShippingMethods() {
        return shippingMethodRepository.findAll()
                .stream()
                .map(shippingMapper::toShippingMethodDTO)
                .collect(Collectors.toList());
    }

    public ShippingMethodDTO getShippingMethodById(Long id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức vận chuyển với ID: " + id));
        return shippingMapper.toShippingMethodDTO(shippingMethod);
    }

    public ShippingMethod getShippingMethodEntityById(Long id) {
        return shippingMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phương thức vận chuyển với ID: " + id));
    }

    @Transactional
    public ShippingMethodDTO createShippingMethod(ShippingMethodDTO dto) {
        ShippingMethod shippingMethod = shippingMapper.toShippingMethodEntity(dto);

        // Đảm bảo ShippingZone tồn tại
        if (dto.getShippingZoneId() != null && dto.getShippingZoneId().getId() != null) {
            shippingZoneRepository.findById(dto.getShippingZoneId().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức vận chuyển với ID: "
                            + dto.getShippingZoneId().getId()));
        }

        ShippingMethod saved = shippingMethodRepository.save(shippingMethod);
        return shippingMapper.toShippingMethodDTO(saved);
    }

    public ShippingMethodDTO updateShippingMethod(Long id, ShippingMethodDTO dto) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy phương thức vận chuyển với ID: " + id));

        if (dto.getMethodName() != null) {
            shippingMethod.setMethodName(dto.getMethodName());
        }
        if (dto.getMaximumOrderWeight() != null) {
            shippingMethod.setMaximumOrderWeight(dto.getMaximumOrderWeight());
        }
        if (dto.getMaximumOrderPriceAmount() != null) {
            shippingMethod.setMaximumOrderPriceAmount(dto.getMaximumOrderPriceAmount());
        }
        if (dto.getMinimumOrderPriceAmount() != null) {
            shippingMethod.setMinimumOrderPriceAmount(dto.getMinimumOrderPriceAmount());
        }
        if (dto.getMinimumOrderWeight() != null) {
            shippingMethod.setMinimumOrderWeight(dto.getMinimumOrderWeight());
        }
        if (dto.getPriceAmount() != null) {
            shippingMethod.setPriceAmount(dto.getPriceAmount());
        }
        if (dto.getCurrency() != null) {
            shippingMethod.setCurrency(dto.getCurrency());
        }
        if (dto.getIsDefault() != null) {
            shippingMethod.setIsDefault(dto.getIsDefault());
        }
        if (dto.getOrders() != null) {
            List<Order> orders = dto.getOrders().stream()
                    .map(orderDTO -> Order.builder()
                            .orderId(orderDTO.getOrderId())
                            .totalAmount(orderDTO.getTotalAmount())
                            .build())
                    .collect(Collectors.toList());
            shippingMethod.setOrders(orders);
        }
        if (dto.getShippingZoneId() != null) {
            ShippingZone shippingZone = ShippingZone.builder()
                    .id(dto.getShippingZoneId().getId())
                    .zoneName(dto.getShippingZoneId().getZoneName())
                    .description(dto.getShippingZoneId().getDescription())
                    .build();
            shippingMethod.setShippingZone(shippingZone);
        }

        shippingMethodRepository.save(shippingMethod);

        return shippingMapper.toShippingMethodDTO(shippingMethod);
    }



    @Transactional
    public void deleteShippingMethod(Long id) {
        if (!shippingMethodRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy phương thức vận chuyển với ID: " + id);
        }
        shippingMethodRepository.deleteById(id);
    }

    // Tra cứu phương thức vận chuyển theo tên
    public ShippingMethod getShippingMethodByName(String name) {
        return shippingMethodRepository.findByMethodName(name)
                .orElseThrow(() -> new IllegalArgumentException("Phương thức vận chuyển không hợp lệ: " + name));
    }

    public ShippingMethod getDefaultShippingMethod() {
        return shippingMethodRepository.findByIsDefaultTrue()
                .orElse(null);
    }


}
