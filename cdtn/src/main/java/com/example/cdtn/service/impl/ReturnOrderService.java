package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.orders.ReturnOrderDTO;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.orders.OrderStatus;
import com.example.cdtn.entity.orders.ReturnOrder;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.OrderMapper;
import com.example.cdtn.repository.BuyerRepository;
import com.example.cdtn.repository.OrderRepository;
import com.example.cdtn.repository.OrderStatusRepository;
import com.example.cdtn.repository.ReturnOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReturnOrderService {
    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BuyerRepository buyerRepository;


    public ReturnOrderDTO createReturnOrder(ReturnOrderDTO returnOrderDTO) {
        // Validate reason
        if (returnOrderDTO.getReason() == null || returnOrderDTO.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do trả hàng không được để trống");
        }

        // Validate buyer
        Buyer buyer = buyerRepository.findById(returnOrderDTO.getBuyer().getBuyerId())
                .orElseThrow(() -> new OurException("Không tìm thấy buyer với ID: " + returnOrderDTO.getBuyer().getBuyerId()));

        // Validate order
        Order order = orderRepository.findById(returnOrderDTO.getOrder().getOrderId())
                .orElseThrow(() -> new OurException("Không tìm thấy order với ID: " + returnOrderDTO.getOrder().getOrderId()));

        // Kiểm tra order thuộc về buyer
        if (!order.getBuyer().getBuyerId().equals(buyer.getBuyerId())) {
            throw new IllegalStateException("Order không thuộc về buyer này");
        }

        // Kiểm tra orderStatus_id = 4 (Đã nhận hàng)
        if (order.getOrderStatus() == null || !order.getOrderStatus().getOrderStatusId().equals(4L)) {
            throw new IllegalStateException("Order phải ở trạng thái 'Đã nhận hàng' (orderStatus_id = 4) để yêu cầu trả hàng");
        }

        // Kiểm tra xem đã tồn tại ReturnOrder cho order này chưa
        if (returnOrderRepository.existsByOrder(order)) {
            throw new IllegalStateException("Order này đã có yêu cầu trả hàng");
        }
        OrderStatus returnStatus = orderStatusRepository.findById(6L)
                .orElseThrow(() -> new OurException("Không tìm thấy trạng thái 'Hoàn trả hàng' (orderStatus_id = 6)"));

        // Tạo mới ReturnOrder
        ReturnOrder returnOrder = orderMapper.toReturnOrderEntity(returnOrderDTO);
        returnOrder.setBuyer(buyer);
        returnOrder.setOrder(order);
        returnOrder.setOrderStatus(returnStatus);
        returnOrder.setCreatedAt(new Date());

        // Cập nhật orderStatus_id của Order thành 6 (Hoàn trả hàng)
        order.setOrderStatus(returnStatus);
        orderRepository.save(order);

        // Lưu ReturnOrder
        ReturnOrder savedReturnOrder = returnOrderRepository.save(returnOrder);

        return orderMapper.toReturnOrderDTO(savedReturnOrder);
    }



    /**Lấy tất cả đơn trả hàng của một khách hàng*/
    public List<ReturnOrderDTO> getReturnOrdersByBuyerId(Long buyerId) {
        // Kiểm tra tồn tại của buyer
        Buyer buyer = buyerService.getBuyerById(buyerId);

        List<ReturnOrder> returnOrders = returnOrderRepository.findByBuyer_BuyerIdOrderByCreatedAtDesc(buyerId);
        return returnOrders.stream()
                .map(orderMapper::toReturnOrderDTO)
                .collect(Collectors.toList());
    }

    /**Lấy chi tiết đơn trả hàng theo ID*/
    public ReturnOrderDTO getReturnOrderById(Long returnId) {
        ReturnOrder returnOrder = returnOrderRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn trả hàng với ID: " + returnId));

        return orderMapper.toReturnOrderDTO(returnOrder);
    }

    /**Lấy đơn trả hàng theo ID đơn hàng gốc*/
    public ReturnOrderDTO getReturnOrderByOrderId(Long orderId) {
        ReturnOrder returnOrder = returnOrderRepository.findByOrderOrderId(orderId);
        if (returnOrder == null) {
            throw new RuntimeException("Không tìm thấy đơn trả hàng cho đơn hàng với ID: " + orderId);
        }

        return orderMapper.toReturnOrderDTO(returnOrder);
    }

    /**Cập nhật trạng thái đơn trả hàng*/
    @Transactional
    public ReturnOrderDTO updateReturnOrderStatus(Long returnId, Long statusId) {
        ReturnOrder returnOrder = returnOrderRepository.findById(returnId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn trả hàng với ID: " + returnId));

        OrderStatus newStatus = orderStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái với ID: " + statusId));

        // Kiểm tra logic chuyển trạng thái
        validateStatusTransition(returnOrder.getOrderStatus().getOrderStatusId(), statusId);

        returnOrder.setOrderStatus(newStatus);

        // Nếu trạng thái là "Chấp nhận trả hàng" (ví dụ statusId = 7)
        if (statusId == 7L) {
            // Cập nhật trạng thái đơn hàng gốc
            Order originalOrder = returnOrder.getOrder();
            originalOrder.setOrderStatus(newStatus);
            orderRepository.save(originalOrder);

            restoreInventory(originalOrder);
        }

        // Lưu đơn trả hàng đã cập nhật
        ReturnOrder updatedReturnOrder = returnOrderRepository.save(returnOrder);
        return orderMapper.toReturnOrderDTO(updatedReturnOrder);
    }

    /**Kiểm tra tính hợp lệ khi chuyển trạng thái*/
    private void validateStatusTransition(Long currentStatusId, Long newStatusId) {
        // Kiểm tra các chuyển trạng thái hợp lệ
        if (currentStatusId == 6L && (newStatusId != 7L && newStatusId != 8L)) {
            throw new IllegalStateException("Không thể chuyển từ trạng thái 'Yêu cầu trả hàng' sang trạng thái với ID: " + newStatusId);
        }

        // Trạng thái đã chấp nhận (7) hoặc từ chối (8) không thể chuyển sang trạng thái khác
        if ((currentStatusId == 7L || currentStatusId == 8L) && !currentStatusId.equals(newStatusId)) {
            throw new IllegalStateException("Không thể thay đổi trạng thái đơn trả hàng đã được xử lý");
        }
    }

    /**Khôi phục tồn kho khi đơn hàng bị trả lại*/
    private void restoreInventory(Order order) {
        order.getOrderItems().forEach(item -> {
            if (item.getProductVariant() != null) {
                ProductVariant variant = item.getProductVariant();
                variant.setQuantity(variant.getQuantity() + item.getQuantity());
            } else if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.setMinimalQuantity(product.getMinimalQuantity() + item.getQuantity());
            }
        });
    }

    /**Lấy danh sách đơn trả hàng theo trạng thái*/
    public List<ReturnOrderDTO> getReturnOrdersByStatus(Long statusId) {
        List<ReturnOrder> returnOrders = returnOrderRepository.findByOrderStatusOrderStatusId(statusId);
        return returnOrders.stream()
                .map(orderMapper::toReturnOrderDTO)
                .collect(Collectors.toList());
    }
}
