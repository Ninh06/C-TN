package com.example.cdtn.controller;

import com.example.cdtn.dtos.orders.CreateOrderRequest;
import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.ships.UpdateShippingRequest;
import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.example.cdtn.service.impl.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**Tạo order từ giỏ hàng*/
    @PostMapping("/create")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> createOrdersFromCart(
            @RequestParam Long buyerId,
            @RequestParam Long shippingAddressId,
            @RequestParam(required = false) Long voucherId,
            @RequestParam(required = false) Long discountId,
            @RequestParam(required = false) Long shippingMethodId
    ) {
        try {
            List<OrderDTO> orderDTOs = orderService.createOrdersFromCart(
                    buyerId, shippingAddressId, voucherId, discountId, shippingMethodId
            );
            return ResponseEntity.ok(orderDTOs);
        } catch (IllegalArgumentException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi trong quá trình tạo đơn hàng.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**
     * Tạo đơn hàng mới trực tiếp (không thông qua giỏ hàng)
     */
    @PostMapping("/create/direct")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderDTO createdOrder = orderService.createOrder(
                    request.getBuyerId(),
                    request.getSellerId(),
                    request.getShippingAddressId(),
                    request.getShippingMethodId(),
                    request.getVoucherId(), // Truyền voucherId
                    request.getDiscountId(), // Truyền discountId
                    request.getOrderItems()
            );
            return ResponseEntity.ok(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi khi tạo đơn hàng: " + e.getMessage()));
        }
    }

    /** Lấy danh sách đơn hàng theo buyerId */
    @GetMapping("/buyer/{buyerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUYER')")
    public ResponseEntity<List<OrderDTO>> getOrdersByBuyerId(@PathVariable Long buyerId) {
        List<OrderDTO> orders = orderService.getOrdersByBuyerId(buyerId);
        return ResponseEntity.ok(orders);
    }

    /** Lấy danh sách đơn hàng theo sellerId */
    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<List<OrderDTO>> getOrdersBySellerId(@PathVariable Long sellerId) {
        List<OrderDTO> orders = orderService.getOrdersBySellerId(sellerId);
        return ResponseEntity.ok(orders);
    }

    /** Lấy chi tiết đơn hàng theo orderId */
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDTO order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi lấy thông tin đơn hàng.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**Lấy danh sách đơn hàng theo trạng thái*/
    @GetMapping("/status/{statusId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public List<OrderDTO> getOrdersByStatus(@PathVariable Long statusId) {
        return orderService.getOrdersByStatus(statusId);
    }

    /** Lọc đơn hàng theo nhiều tiêu chí*/
    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public List<OrderDTO> filterOrders(
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date toDate) {
        System.out.println("fromDate: " + fromDate); // Debug log
        System.out.println("toDate: " + toDate);
        return orderService.filterOrders(buyerId, sellerId, statusId, fromDate, toDate);
    }

    /**Cập nhật trạng thái đơn hàng */
    @PutMapping("/{orderId}/status/{statusId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable Long statusId,
            @RequestBody(required = false) Map<String, String> payload) {

        try {
            // Nếu là trạng thái yêu cầu lý do (Hủy - 5L, Trả hàng - 6L)
            String reason = null;
            if (statusId == 6L) {
                if (payload == null || !payload.containsKey("reason") || payload.get("reason").trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Lý do không được để trống cho trạng thái " + statusId);
                }
                reason = payload.get("reason");
            }

            OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, statusId, reason);
            return ResponseEntity.ok(updatedOrder);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/seller-revenue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<?> calculateSellerRevenue(
            @RequestParam Long sellerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date toDate) {
        try {
            String resultMessage = orderService.calculateSellerRevenue(sellerId, fromDate, toDate);
            return ResponseEntity.ok(resultMessage);
        } catch (IllegalArgumentException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse("Đã xảy ra lỗi khi tính doanh thu.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/update-shipping")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BUYER')")
    public ResponseEntity<?> updateOrderShipping(@RequestBody UpdateShippingRequest request) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderShipping(
                    request.getOrderId(),
                    request.getShippingAddressId(),
                    request.getShippingMethodId()
            );
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Không cập nhật được thông tin vận chuyển: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
