package com.example.cdtn.repository;

import com.example.cdtn.entity.discounts.DiscountVoucher;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.orders.OrderStatus;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.entity.users.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByBuyerAndVoucher(Buyer buyer, DiscountVoucher voucher);

    /**Tìm tất cả đơn hàng của một buyer*/
    List<Order> findByBuyerOrderByCreatedAtDesc(Buyer buyer);

    /**Tìm tất cả đơn hàng của một seller*/
    List<Order> findBySellerOrderByCreatedAtDesc(Seller seller);

    /**Tìm tất cả đơn hàng của một buyer*/
    List<Order> findByBuyer(Buyer buyer);

    /** Tìm tất cả đơn hàng của một seller */
    List<Order> findBySeller(Seller seller);

    /**Tìm tất cả đơn hàng có trạng thái nhất định */
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    /**Tìm đơn hàng của seller trong khoảng thời gian và có trạng thái cụ thể*/
    List<Order> findBySellerAndCreatedAtBetweenAndOrderStatus_OrderStatusIdOrderByCreatedAtDesc(
            Seller seller, Date fromDate, Date toDate, Long statusId);

    /**Query tùy chỉnh để lọc đơn hàng theo nhiều tiêu chí*/
    @Query("SELECT o FROM Order o WHERE " +
            "(:buyerId IS NULL OR o.buyer.id = :buyerId) AND " +
            "(:sellerId IS NULL OR o.seller.id = :sellerId) AND " +
            "(:statusId IS NULL OR o.orderStatus.id = :statusId) AND " +
            "(:fromDate IS NULL OR o.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR o.createdAt <= :toDate) " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByFilters(
            @Param("buyerId") Long buyerId,
            @Param("sellerId") Long sellerId,
            @Param("statusId") Long statusId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    /**Tìm các đơn hàng được tạo trong khoảng thời gian*/
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(Date fromDate, Date toDate);

    /**Đếm số lượng đơn hàng theo trạng thái*/
    long countByOrderStatus(OrderStatus orderStatus);

    /**Đếm số lượng đơn hàng của một buyer theo trạng thái*/
    long countByBuyerAndOrderStatus(Buyer buyer, OrderStatus orderStatus);

    /**Đếm số lượng đơn hàng của một seller theo trạng thái*/
    long countBySellerAndOrderStatus(Seller seller, OrderStatus orderStatus);

    /**Tìm các đơn hàng có tổng tiền lớn hơn một giá trị cụ thể*/
    List<Order> findByTotalAmountGreaterThanEqual(java.math.BigDecimal amount);

    /**Tìm đơn hàng mới nhất của một buyer*/
    Order findFirstByBuyerOrderByCreatedAtDesc(Buyer buyer);

    /**Tìm đơn hàng mới nhất của một seller*/
    Order findFirstBySellerOrderByCreatedAtDesc(Seller seller);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.seller.id = :sellerId " +
            "AND o.orderStatus.id = 4 " +
            "AND (:fromDate IS NULL OR o.createdAt >= :fromDate) " +
            "AND (:toDate IS NULL OR o.createdAt <= :toDate)")
    BigDecimal calculateRevenueBySeller(
            @Param("sellerId") Long sellerId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    List<Order> findByBuyerAndOrderStatus_OrderStatusId(Buyer buyer, Long orderStatusId);
}
