package com.example.cdtn.repository;

import com.example.cdtn.entity.discounts.DiscountVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountVoucherRepository extends JpaRepository<DiscountVoucher, Long> {

    /**Tìm voucher theo mã code*/
    Optional<DiscountVoucher> findByVoucherCode(String voucherCode);

    /**Kiểm tra voucher đã tồn tại chưa theo mã code*/
    boolean existsByVoucherCode(String voucherCode);

    /**Tìm các voucher có hiệu lực trong khoảng thời gian hiện tại */
    List<DiscountVoucher> findByStartDateBeforeAndEndDateAfter(Date startBefore, Date endAfter);

    /**Tìm các voucher sắp hết hạn*/
    List<DiscountVoucher> findByEndDateBetween(Date currentDate, Date expirationDate);

    /**Tìm các voucher theo phần trăm giảm giá */
    List<DiscountVoucher> findByDiscountPercentage(Double percentage);

    /** Tìm các voucher có phần trăm giảm giá lớn hơn hoặc bằng giá trị cung cấp*/
    List<DiscountVoucher> findByDiscountPercentageGreaterThanEqual(Double percentage);

    /**Kiểm tra xem khách hàng đã sử dụng voucher này chưa*/
    @Query(value = "SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM orders o " +
            "JOIN discount_voucher v ON o.voucher_id = v.voucher_id " +
            "WHERE v.voucher_code = :voucherCode AND o.customer_id = :customerId", nativeQuery = true)
    boolean hasCustomerUsedVoucher(@Param("voucherCode") String voucherCode, @Param("customerId") Long customerId);

    /**Đếm số lần voucher đã được sử dụng*/
    @Query(value = "SELECT COUNT(o.order_id) FROM orders o " +
            "JOIN discount_voucher v ON o.voucher_id = v.voucher_id " +
            "WHERE v.voucher_code = :voucherCode", nativeQuery = true)
    Long countVoucherUsage(@Param("voucherCode") String voucherCode);

    /**Tìm các voucher đã tạo trong khoảng thời gian*/
    List<DiscountVoucher> findByCreatedAtBetween(Date startDate, Date endDate);

    /**Xóa các voucher đã hết hạn*/
    @Modifying
    @Query("DELETE FROM DiscountVoucher v WHERE v.endDate < :currentDate")
    int deleteExpiredVouchers(@Param("currentDate") Date currentDate);
}