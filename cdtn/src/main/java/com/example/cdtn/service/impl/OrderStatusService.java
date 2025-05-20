package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.orders.OrderStatusDTO;
import com.example.cdtn.entity.orders.OrderStatus;
import com.example.cdtn.mapper.OrderMapper;
import com.example.cdtn.repository.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderStatusService {
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private OrderMapper orderMapper;

    /**Lấy tất cả trạng thái đơn hàng*/
    public List<OrderStatusDTO> getAllOrderStatuses() {
        List<OrderStatus> orderStatuses = orderStatusRepository.findAll();
        return orderStatuses.stream()
                .map(orderMapper::toOrderStatusDTO)
                .collect(Collectors.toList());
    }

    /** Lấy trạng thái đơn hàng theo ID*/
    public OrderStatusDTO getOrderStatusById(Long id) {
        OrderStatus orderStatus = orderStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái đơn hàng với ID: " + id));
        return orderMapper.toOrderStatusDTO(orderStatus);
    }

    /**Lấy danh sách trạng thái đơn hàng với phân trang*/
    public Page<OrderStatusDTO> getAllOrderStatusesWithPagination(Pageable pageable) {
        Page<OrderStatus> orderStatusesPage = orderStatusRepository.findAll(pageable);
        return orderStatusesPage.map(orderMapper::toOrderStatusDTO);
    }

    /**Tìm kiếm trạng thái đơn hàng theo mô tả*/
    public List<OrderStatusDTO> findByDescription(String description) {
        List<OrderStatus> orderStatuses = orderStatusRepository.findByOrderStatusDescContainingIgnoreCase(description);
        return orderStatuses.stream()
                .map(orderMapper::toOrderStatusDTO)
                .collect(Collectors.toList());
    }

    /**Tạo mới trạng thái đơn hàng*/
    @Transactional
    public OrderStatusDTO createOrderStatus(OrderStatusDTO orderStatusDTO) {
        OrderStatus orderStatus = orderMapper.toOrderStatus(orderStatusDTO);
        OrderStatus savedOrderStatus = orderStatusRepository.save(orderStatus);
        return orderMapper.toOrderStatusDTO(savedOrderStatus);
    }

    /**Cập nhật trạng thái đơn hàng*/
    @Transactional
    public OrderStatusDTO updateOrderStatus(Long id, OrderStatusDTO orderStatusDTO) {
        OrderStatus existingOrderStatus = orderStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái đơn hàng với ID: " + id));

        // Cập nhật thông tin
        existingOrderStatus.setOrderStatusDesc(orderStatusDTO.getOrderStatusDesc());

        // Lưu và trả về kết quả
        OrderStatus updatedOrderStatus = orderStatusRepository.save(existingOrderStatus);
        return orderMapper.toOrderStatusDTO(updatedOrderStatus);
    }

    /**Xóa trạng thái đơn hàng theo ID*/
    @Transactional
    public void deleteOrderStatus(Long id) {
        if (!orderStatusRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy trạng thái đơn hàng với ID: " + id);
        }
        orderStatusRepository.deleteById(id);
    }

}
