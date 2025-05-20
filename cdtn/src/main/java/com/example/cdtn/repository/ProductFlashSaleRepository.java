package com.example.cdtn.repository;

import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductFlashSaleRepository extends JpaRepository<ProductFlashSale, Long> {
    List<ProductFlashSale> findByFlashSale_FlashSaleId(Long flashSaleId);

    /**Tìm tất cả các flash sale mà một sản phẩm tham gia*/
    List<ProductFlashSale> findByProduct_ProductId(Long productId);

    /** Tìm thông tin flash sale của một sản phẩm cụ thể trong một flash sale cụ thể */
    Optional<ProductFlashSale> findByProduct_ProductIdAndFlashSale_FlashSaleId(Long productId, Long flashSaleId);

    /**Tìm tất cả các flash sale đang active mà một sản phẩm tham gia tại thời điểm hiện tại*/
    @Query("SELECT pfs FROM ProductFlashSale pfs " +
            "JOIN pfs.flashSale fs " +
            "WHERE pfs.product.productId = :productId " +
            "AND fs.isActive = true " +
            "AND fs.startTime <= :currentTime " +
            "AND fs.endTime >= :currentTime")
    List<ProductFlashSale> findActiveFlashSalesByProductId(
            @Param("productId") Long productId,
            @Param("currentTime") Date currentTime);

    /** Kiểm tra xem một sản phẩm có đang tham gia vào bất kỳ flash sale nào đang active không*/
    @Query("SELECT COUNT(pfs) > 0 FROM ProductFlashSale pfs " +
            "JOIN pfs.flashSale fs " +
            "WHERE pfs.product.productId = :productId " +
            "AND fs.isActive = true " +
            "AND fs.startTime <= :currentTime " +
            "AND fs.endTime >= :currentTime")
    boolean isProductInActiveFlashSale(
            @Param("productId") Long productId,
            @Param("currentTime") Date currentTime);

    /**Tìm tất cả các sản phẩm đang tham gia flash sale tại thời điểm hiện tại */
    @Query("SELECT DISTINCT pfs.product FROM ProductFlashSale pfs " +
            "JOIN pfs.flashSale fs " +
            "WHERE fs.isActive = true " +
            "AND fs.startTime <= :currentTime " +
            "AND fs.endTime >= :currentTime " +
            "AND pfs.soldCount < pfs.quota")
    List<Product> findProductsInActiveFlashSale(@Param("currentTime") Date currentTime);

    void deleteByFlashSale_FlashSaleId(Long flashSaleId);

    Optional<ProductFlashSale> findByFlashSale_FlashSaleIdAndProduct_ProductId(Long flashSaleId, Long productId);

}
