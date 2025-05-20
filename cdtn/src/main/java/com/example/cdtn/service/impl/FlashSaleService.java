package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.flashsale.FlashSaleDTO;
import com.example.cdtn.dtos.flashsale.ProductFlashSaleDTO;
import com.example.cdtn.entity.flashsale.FlashSale;
import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.flashsale.ProductVariantFlashSale;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.FlashSaleMapper;
import com.example.cdtn.repository.FlashSaleRepository;
import com.example.cdtn.repository.ProductFlashSaleRepository;
import com.example.cdtn.repository.ProductRepository;
import com.example.cdtn.repository.ProductVariantFlashSaleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashSaleService {
    @Autowired
    private FlashSaleRepository flashSaleRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductFlashSaleRepository productFlashSaleRepository;
    @Autowired
    private FlashSaleMapper flashSaleMapper;
    @Autowired
    private ProductVariantFlashSaleRepository productVariantFlashSaleRepository;

    @Transactional
    public FlashSaleDTO createFlashSale(FlashSaleDTO flashSaleDTO) throws BadRequestException {
        try {
            validateFlashSale(flashSaleDTO);

            // Kiểm tra flash sale trùng lặp
            List<FlashSale> overlappingActiveFlashSales = flashSaleRepository.findOverlappingActiveFlashSales(
                    flashSaleDTO.getStartTime(),
                    flashSaleDTO.getEndTime());
            if (!overlappingActiveFlashSales.isEmpty()) {
                throw new BadRequestException("Đã có flash sale đang hoạt động trong khoảng thời gian này.");
            }

            // Xác định trạng thái flash sale
            determineFlashSaleStatus(flashSaleDTO);

            // Chuyển đổi DTO thành entity
            FlashSale flashSale = new FlashSale();
            flashSale.setName(flashSaleDTO.getName());
            flashSale.setDescription(flashSaleDTO.getDescription());
            flashSale.setStartTime(flashSaleDTO.getStartTime());
            flashSale.setEndTime(flashSaleDTO.getEndTime());
            flashSale.setStatus(flashSaleDTO.getStatus());
            flashSale.setIsActive(flashSaleDTO.getIsActive());

            // Lưu FlashSale
            FlashSale savedFlashSale = flashSaleRepository.saveAndFlush(flashSale);
            System.out.println("FlashSale ID được tạo: " + savedFlashSale.getFlashSaleId());

            // Xử lý ProductFlashSale
            if (flashSaleDTO.getProductFlashSales() != null && !flashSaleDTO.getProductFlashSales().isEmpty()) {
                List<ProductFlashSale> allProductFlashSales = new ArrayList<>();
                for (ProductFlashSaleDTO productFlashSaleDTO : flashSaleDTO.getProductFlashSales()) {
                    Long productId = productFlashSaleDTO.getProduct().getProductId();
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm với ID: " + productId));

                    ProductFlashSale productFlashSale = new ProductFlashSale();
                    productFlashSale.setQuota(productFlashSaleDTO.getQuota());
                    productFlashSale.setDiscountPercentage(productFlashSaleDTO.getDiscountPercentage());
                    productFlashSale.setSoldCount(0);
                    productFlashSale.setIsActive(flashSaleDTO.getIsActive()); // Đồng bộ trạng thái
                    productFlashSale.setProduct(product);
                    productFlashSale.setFlashSale(savedFlashSale);

                    if (product.getMinimalVariantPriceAmount() != null) {
                        productFlashSale.setOriginalPrice(product.getMinimalVariantPriceAmount());
                    }

                    if (productFlashSale.getDiscountPercentage() != null && productFlashSale.getOriginalPrice() != null) {
                        double discountFactor = productFlashSale.getDiscountPercentage() > 1 ?
                                1 - (productFlashSale.getDiscountPercentage() / 100.0) :
                                1 - productFlashSale.getDiscountPercentage();
                        double flashSalePrice = productFlashSale.getOriginalPrice() * discountFactor;
                        productFlashSale.setFlashSalePrice(flashSalePrice);
                    }

                    validateProductFlashSale(productFlashSale);
                    ProductFlashSale savedProductFlashSale = productFlashSaleRepository.saveAndFlush(productFlashSale);
                    allProductFlashSales.add(savedProductFlashSale);

                    // Xử lý ProductVariantFlashSale
                    if (product.getProductVariants() != null && !product.getProductVariants().isEmpty()) {
                        double discountFactor = productFlashSale.getDiscountPercentage() > 1 ?
                                1 - (productFlashSale.getDiscountPercentage() / 100.0) :
                                1 - productFlashSale.getDiscountPercentage();
                        List<ProductVariantFlashSale> variantFlashSales = new ArrayList<>();
                        for (ProductVariant variant : product.getProductVariants()) {
                            BigDecimal originalPrice = variant.getPrice();
                            BigDecimal flashSalePrice = originalPrice.multiply(
                                    new BigDecimal(discountFactor)
                            ).setScale(2, RoundingMode.HALF_UP);

                            ProductVariantFlashSale variantFlashSale = new ProductVariantFlashSale();
                            variantFlashSale.setProductVariant(variant);
                            variantFlashSale.setProductFlashSale(savedProductFlashSale);
                            variantFlashSale.setOriginalPrice(originalPrice.doubleValue());
                            variantFlashSale.setFlashSalePrice(flashSalePrice.doubleValue());
                            variantFlashSale.setDiscountPercentage(productFlashSale.getDiscountPercentage());
                            variantFlashSale.setIsActive(flashSaleDTO.getIsActive()); // Đồng bộ trạng thái
                            variantFlashSales.add(variantFlashSale);
                        }
                        if (!variantFlashSales.isEmpty()) {
                            productVariantFlashSaleRepository.saveAll(variantFlashSales);
                        }
                    }
                }
                savedFlashSale.setProductFlashSales(allProductFlashSales);
            }

            FlashSale finalFlashSale = flashSaleRepository.findById(savedFlashSale.getFlashSaleId())
                    .orElseThrow(() -> new OurException("Không thể tìm thấy Flash Sale vừa tạo"));
            return flashSaleMapper.toFlashSaleDTO(finalFlashSale);
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo Flash Sale: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**Thêm sản phẩm vào FlashSale hiện có*/
    /**Thêm sản phẩm vào FlashSale hiện có*/
    @Transactional
    public ProductFlashSaleDTO addProductToFlashSale(Long flashSaleId, Long productId, Integer quota, Double discountPercentage)
            throws BadRequestException {
        try {
            // Tìm FlashSale
            FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy FlashSale với ID: " + flashSaleId));

            // Kiểm tra trạng thái của FlashSale
            if ("ENDED".equals(flashSale.getStatus())) {
                throw new BadRequestException("Không thể thêm sản phẩm vào FlashSale đã kết thúc");
            }

            // Tìm Product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy sản phẩm với ID: " + productId));

            // Kiểm tra xem sản phẩm đã có trong FlashSale chưa
            Optional<ProductFlashSale> existingProductFlashSale = productFlashSaleRepository
                    .findByFlashSale_FlashSaleIdAndProduct_ProductId(flashSaleId, productId);
            if (existingProductFlashSale.isPresent()) {
                throw new BadRequestException("Sản phẩm đã tồn tại trong FlashSale này");
            }

            // Tạo đối tượng ProductFlashSale mới
            ProductFlashSale productFlashSale = new ProductFlashSale();
            productFlashSale.setQuota(quota);
            productFlashSale.setDiscountPercentage(discountPercentage);
            productFlashSale.setSoldCount(0);
            productFlashSale.setIsActive(flashSale.getIsActive()); // Đồng bộ trạng thái với FlashSale
            productFlashSale.setProduct(product);
            productFlashSale.setFlashSale(flashSale);

            if (product.getMinimalVariantPriceAmount() != null) {
                productFlashSale.setOriginalPrice(product.getMinimalVariantPriceAmount());
            }

            if (productFlashSale.getDiscountPercentage() != null && productFlashSale.getOriginalPrice() != null) {
                double discountFactor = productFlashSale.getDiscountPercentage() > 1 ?
                        1 - (productFlashSale.getDiscountPercentage() / 100.0) :
                        1 - productFlashSale.getDiscountPercentage();
                double flashSalePrice = productFlashSale.getOriginalPrice() * discountFactor;
                productFlashSale.setFlashSalePrice(flashSalePrice);
            }

            // Kiểm tra tính hợp lệ của ProductFlashSale
            validateProductFlashSale(productFlashSale);

            // Lưu ProductFlashSale
            ProductFlashSale savedProductFlashSale = productFlashSaleRepository.saveAndFlush(productFlashSale);

            // Xử lý ProductVariantFlashSale cho từng biến thể của sản phẩm
            if (product.getProductVariants() != null && !product.getProductVariants().isEmpty()) {
                double discountFactor = productFlashSale.getDiscountPercentage() > 1 ?
                        1 - (productFlashSale.getDiscountPercentage() / 100.0) :
                        1 - productFlashSale.getDiscountPercentage();
                List<ProductVariantFlashSale> variantFlashSales = new ArrayList<>();

                for (ProductVariant variant : product.getProductVariants()) {
                    BigDecimal originalPrice = variant.getPrice();
                    BigDecimal flashSalePrice = originalPrice.multiply(
                            new BigDecimal(discountFactor)
                    ).setScale(2, RoundingMode.HALF_UP);

                    ProductVariantFlashSale variantFlashSale = new ProductVariantFlashSale();
                    variantFlashSale.setProductVariant(variant);
                    variantFlashSale.setProductFlashSale(savedProductFlashSale);
                    variantFlashSale.setOriginalPrice(originalPrice.doubleValue());
                    variantFlashSale.setFlashSalePrice(flashSalePrice.doubleValue());
                    variantFlashSale.setDiscountPercentage(productFlashSale.getDiscountPercentage());
                    variantFlashSale.setIsActive(flashSale.getIsActive()); // Đồng bộ trạng thái
                    variantFlashSales.add(variantFlashSale);
                }

                if (!variantFlashSales.isEmpty()) {
                    productVariantFlashSaleRepository.saveAll(variantFlashSales);
                }
            }

            // Thêm ProductFlashSale mới vào collection hiện tại
            flashSale.getProductFlashSales().add(savedProductFlashSale);
            flashSaleRepository.save(flashSale);

            // Trả về DTO của ProductFlashSale đã lưu
            return flashSaleMapper.toProductFlashSaleDTO(savedProductFlashSale);
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm sản phẩm vào FlashSale: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**Vô hiệu hóa ProductFlashSale và các ProductVariantFlashSale liên quan*/
    @Transactional
    public ProductFlashSaleDTO deactivateProductFlashSale(Long productFlashSaleId) throws BadRequestException {
        try {
            // Tìm ProductFlashSale
            ProductFlashSale productFlashSale = productFlashSaleRepository.findById(productFlashSaleId)
                    .orElseThrow(() ->
                            new BadRequestException("Không tìm thấy ProductFlashSale với ID: " + productFlashSaleId));

            // Cập nhật isActive của ProductFlashSale
            productFlashSale.setIsActive(false);

            // Lưu ProductFlashSale
            ProductFlashSale updatedProductFlashSale = productFlashSaleRepository.saveAndFlush(productFlashSale);

            // Cập nhật isActive của tất cả ProductVariantFlashSale liên quan (nếu có)
            List<ProductVariantFlashSale> variantFlashSales = productVariantFlashSaleRepository
                    .findByProductFlashSaleId(productFlashSaleId);
            if (!variantFlashSales.isEmpty()) {
                for (ProductVariantFlashSale variantFlashSale : variantFlashSales) {
                    variantFlashSale.setIsActive(false);
                }
                productVariantFlashSaleRepository.saveAll(variantFlashSales);
            }

            // Trả về DTO của ProductFlashSale đã cập nhật
            return flashSaleMapper.toProductFlashSaleDTO(updatedProductFlashSale);
        } catch (Exception e) {
            System.err.println("Lỗi khi vô hiệu hóa ProductFlashSale: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Tính giá flash sale dựa vào phần trăm giảm giá và giá gốc
     */
    private void calculateFlashSalePriceFromDiscount(ProductFlashSale productFlashSale) {
        double discountFactor;

        // Xử lý trường hợp người dùng nhập discountPercentage dạng phần trăm (ví dụ: 10) hoặc dạng thập phân (ví dụ: 0.1)
        if (productFlashSale.getDiscountPercentage() > 1) {
            // Nếu lớn hơn 1, giả định là phần trăm (ví dụ: 10 = 10%)
            discountFactor = 1 - (productFlashSale.getDiscountPercentage() / 100.0);
        } else {
            // Nếu nhỏ hơn hoặc bằng 1, giả định là thập phân (ví dụ: 0.1 = 10%)
            discountFactor = 1 - productFlashSale.getDiscountPercentage();
        }

        // Tính giá flash sale dựa trên giá gốc và tỷ lệ giảm giá
        double flashSalePrice = productFlashSale.getOriginalPrice() * discountFactor;

        // Làm tròn giá flash sale nếu cần
        productFlashSale.setFlashSalePrice(flashSalePrice);
    }

    /**
     * Tính phần trăm giảm giá dựa vào giá flash sale và giá gốc
     */
    private void calculateDiscountPercentageFromPrice(ProductFlashSale productFlashSale) {
        double discountPercentage = (1 - (productFlashSale.getFlashSalePrice()
                / productFlashSale.getOriginalPrice())) * 100;
        double roundedDiscountPercentage = (int) Math.round(discountPercentage);
        productFlashSale.setDiscountPercentage(roundedDiscountPercentage);
    }

    /**
     * Kiểm tra tính hợp lệ của flash sale
     */
    private void validateFlashSale(FlashSaleDTO flashSaleDTO) throws BadRequestException {
        if (flashSaleDTO.getName() == null || flashSaleDTO.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên flash sale không được để trống");
        }

        if (flashSaleDTO.getStartTime() == null) {
            throw new BadRequestException("Thời gian bắt đầu không được để trống");
        }

        if (flashSaleDTO.getEndTime() == null) {
            throw new BadRequestException("Thời gian kết thúc không được để trống");
        }

        if (flashSaleDTO.getStartTime().after(flashSaleDTO.getEndTime())) {
            throw new BadRequestException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        // Kiểm tra thời lượng tối thiểu và tối đa của flash sale nếu cần
        long durationInMillis = flashSaleDTO.getEndTime().getTime() - flashSaleDTO.getStartTime().getTime();
        long durationInHours = durationInMillis / (60 * 60 * 1000);

        if (durationInHours < 1) {
            throw new BadRequestException("Flash sale phải kéo dài ít nhất 1 giờ");
        }

        if (durationInHours > 24) {
            throw new BadRequestException("Flash sale không được kéo dài quá 24 giờ");
        }
    }

    /**
     * Kiểm tra tính hợp lệ của sản phẩm flash sale
     */
    private void validateProductFlashSale(ProductFlashSale productFlashSale) throws BadRequestException {
        // Kiểm tra originalPrice (có thể lấy từ sản phẩm)
        if (productFlashSale.getOriginalPrice() == null) {
            throw new BadRequestException("Không thể xác định giá gốc cho sản phẩm");
        }

        // Kiểm tra nếu không có cả flashSalePrice và discountPercentage
        if (productFlashSale.getFlashSalePrice() == null && productFlashSale.getDiscountPercentage() == null) {
            throw new BadRequestException("Phải cung cấp ít nhất một trong hai: giá flash sale hoặc phần trăm giảm giá");
        }

        // Đảm bảo giá flash sale đã được tính toán nếu chỉ có discountPercentage
        if (productFlashSale.getFlashSalePrice() == null && productFlashSale.getDiscountPercentage() != null) {
            calculateFlashSalePriceFromDiscount(productFlashSale);
        }

        // Kiểm tra số lượng sản phẩm tham gia flash sale
        if (productFlashSale.getQuota() == null) {
            throw new BadRequestException("Số lượng sản phẩm tham gia flash sale không được để trống");
        }

        if (productFlashSale.getQuota() <= 0) {
            throw new BadRequestException("Số lượng sản phẩm tham gia flash sale phải lớn hơn 0");
        }

        // Kiểm tra giá flash sale có hợp lệ không
        if (productFlashSale.getFlashSalePrice() <= 0) {
            throw new BadRequestException("Giá flash sale phải lớn hơn 0");
        }

        // Kiểm tra giá flash sale có nhỏ hơn giá gốc không
        if (productFlashSale.getFlashSalePrice() >= productFlashSale.getOriginalPrice()) {
            throw new BadRequestException("Giá flash sale phải nhỏ hơn giá gốc");
        }

        // Đảm bảo discountPercentage đã được tính toán nếu chỉ có flashSalePrice
        if (productFlashSale.getDiscountPercentage() == null) {
            calculateDiscountPercentageFromPrice(productFlashSale);
        }
    }

    @Transactional
    public void handleExpiredFlashSales() {
        Date now = new Date();

        // Tìm các FlashSale đã kết thúc nhưng vẫn đang active
        List<FlashSale> expiredFlashSales = flashSaleRepository.findByEndTimeBeforeAndIsActiveTrue(now);

        for (FlashSale flashSale : expiredFlashSales) {
            Long flashSaleId = flashSale.getFlashSaleId();

            // --- Xóa ProductVariantFlashSale trước ---
            List<ProductFlashSale> productFlashSales = productFlashSaleRepository.findByFlashSale_FlashSaleId(flashSaleId);
            for (ProductFlashSale pfs : productFlashSales) {
                productVariantFlashSaleRepository.deleteByProductFlashSaleId(pfs.getId());
            }

            // --- Xóa ProductFlashSale ---
            productFlashSaleRepository.deleteByFlashSale_FlashSaleId(flashSaleId);

            // --- Cập nhật FlashSale: status = ENDED, isActive = false ---
            flashSale.setStatus("ENDED");
            flashSale.setIsActive(false);
            flashSaleRepository.save(flashSale);
        }
    }

    /**
     * Phương thức để kích hoạt các FlashSale từ trạng thái UPCOMING sang STARTING khi đến thời gian bắt đầu
     */
    @Transactional
    public void activateUpcomingFlashSales() {
        Date now = new Date();

        // Tìm các FlashSale đã đến thời gian bắt đầu nhưng vẫn ở trạng thái UPCOMING
        List<FlashSale> upcomingFlashSales = flashSaleRepository.findByStartTimeBeforeAndEndTimeAfterAndStatus(
                now, now, "UPCOMING");

        for (FlashSale flashSale : upcomingFlashSales) {
            Long flashSaleId = flashSale.getFlashSaleId();

            // Cập nhật FlashSale sang trạng thái STARTING và isActive = true
            flashSale.setStatus("STARTING");
            flashSale.setIsActive(true);
            flashSaleRepository.save(flashSale);

            // Cập nhật ProductFlashSale -> isActive = true
            List<ProductFlashSale> productFlashSales = productFlashSaleRepository.findByFlashSale_FlashSaleId(flashSaleId);
            for (ProductFlashSale pfs : productFlashSales) {
                pfs.setIsActive(true);
                productFlashSaleRepository.save(pfs);

                // Cập nhật ProductVariantFlashSale -> isActive = true
                List<ProductVariantFlashSale> variantFlashSales =
                        productVariantFlashSaleRepository.findByProductFlashSaleId(pfs.getId());
                for (ProductVariantFlashSale pvfs : variantFlashSales) {
                    pvfs.setIsActive(true);
                    productVariantFlashSaleRepository.save(pvfs);
                }
            }
        }
    }

    private void determineFlashSaleStatus(FlashSaleDTO flashSaleDTO) {
        try {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            Instant currentInstant = now.toInstant();
            Instant startInstant = flashSaleDTO.getStartTime().toInstant();
            Instant endInstant = flashSaleDTO.getEndTime().toInstant();

            System.out.println("Current Time (Instant): " + currentInstant);
            System.out.println("Start Time (Instant): " + startInstant);
            System.out.println("End Time (Instant): " + endInstant);

            boolean isActive;
            String status;

            if (currentInstant.isBefore(startInstant)) {
                status = "UPCOMING";
                isActive = false;
            } else if (currentInstant.isAfter(endInstant)) {
                status = "ENDED";
                isActive = false;
            } else {
                status = "STARTING";
                isActive = true;
            }

            flashSaleDTO.setStatus(status);
            flashSaleDTO.setIsActive(isActive);
        } catch (Exception e) {
            System.err.println("Lỗi khi xác định trạng thái FlashSale: " + e.getMessage());
            flashSaleDTO.setStatus("UPCOMING");
            flashSaleDTO.setIsActive(false);
        }
    }


}
