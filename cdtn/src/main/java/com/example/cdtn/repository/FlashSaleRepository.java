package com.example.cdtn.repository;

import com.example.cdtn.entity.flashsale.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {
    /** Tìm tất cả các flash sale đang active trong khoảng thời gian được chỉ định */
    @Query("SELECT fs FROM FlashSale fs WHERE fs.isActive = true AND " +
            "((fs.startTime <= :endTime AND fs.endTime >= :startTime))")
    List<FlashSale> findOverlappingActiveFlashSales(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**Tìm tất cả các flash sale có trạng thái active */
    List<FlashSale> findByIsActiveTrue();

    /**Tìm tất cả các flash sale đang diễn ra tại thời điểm hiện tại */
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime <= :currentTime AND fs.endTime >= :currentTime")
    List<FlashSale> findCurrentFlashSales(@Param("currentTime") Date currentTime);

    /**Tìm tất cả các flash sale sắp diễn ra */
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime > :currentTime")
    List<FlashSale> findUpcomingFlashSales(@Param("currentTime") Date currentTime);

    List<FlashSale> findByEndTimeBeforeAndIsActiveTrue(Date currentTime);

    List<FlashSale> findByStartTimeBeforeAndEndTimeAfterAndStatus(Date startTime, Date endTime, String status);
}
