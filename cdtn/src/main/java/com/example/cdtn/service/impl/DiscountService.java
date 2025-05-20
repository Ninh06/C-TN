package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.discounts.DiscountDTO;
import com.example.cdtn.entity.discounts.Discount;
import com.example.cdtn.mapper.DiscountMapper;
import com.example.cdtn.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private DiscountMapper discountMapper;

    /**Kiểm tra tính hợp lệ của dữ liệu chương trình giảm giá*/
    private void validateDiscountData(DiscountDTO discountDTO) {
        if (discountDTO.getDiscountName() == null || discountDTO.getDiscountName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chương trình giảm giá không được để trống");
        }

        if (discountDTO.getDiscountPercentage() == null) {
            throw new IllegalArgumentException("Tỷ lệ giảm giá không được để trống");
        }

        if (discountDTO.getStartDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }

        if (discountDTO.getEndDate() == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống");
        }

        if (discountDTO.getStartDate().after(discountDTO.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }
    }


    /**Tạo mới một chương trình giảm giá*/
    @Transactional
    public DiscountDTO createDiscount(DiscountDTO discountDTO) {
        // Kiểm tra dữ liệu đầu vào
        validateDiscountData(discountDTO);

        Discount discount = discountMapper.toDiscountEntity(discountDTO);

        Discount savedDiscount = discountRepository.save(discount);

        return discountMapper.toDiscountDTO(savedDiscount);
    }

    /**Lấy thông tin một chương trình giảm giá theo ID*/
    @Transactional(readOnly = true)
    public DiscountDTO getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình giảm giá với ID: " + id));
        return discountMapper.toDiscountDTO(discount);
    }

    @Transactional(readOnly = true)
    public Discount getDiscountEntityById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình giảm giá với ID: " + id));
    }

    /**Lấy danh sách tất cả các chương trình giảm giá*/
    @Transactional(readOnly = true)
    public List<DiscountDTO> getAllDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        return discounts.stream()
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }

    /**Cập nhật thông tin một chương trình giảm giá */
    @Transactional
    public DiscountDTO updateDiscount(Long discountId, DiscountDTO discountDTO) {
        // Lấy bản ghi gốc từ DB bằng discountId truyền vào
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình giảm giá với ID: " + discountId));

        if (discountDTO.getDiscountName() != null) {
            existingDiscount.setDiscountName(discountDTO.getDiscountName());
        }
        if (discountDTO.getStartDate() != null) {
            existingDiscount.setStartDate(discountDTO.getStartDate());
        }
        if (discountDTO.getEndDate() != null) {
            existingDiscount.setEndDate(discountDTO.getEndDate());
        }
        if (discountDTO.getDiscountPercentage() != null) {
            existingDiscount.setDiscountPercentage(discountDTO.getDiscountPercentage());
        }
        if (discountDTO.getMinOrderValue() != null) {
            existingDiscount.setMinOrderValue(discountDTO.getMinOrderValue());
        }

        Discount updatedDiscount = discountRepository.save(existingDiscount);
        return discountMapper.toDiscountDTO(updatedDiscount);
    }



    /** Xóa một chương trình giảm giá*/
    @Transactional
    public void deleteDiscount(Long id) {
        // Kiểm tra xem discount có tồn tại không
        Discount existingDiscount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình giảm giá với ID: " + id));

        discountRepository.delete(existingDiscount);
    }

    /**Lấy danh sách các chương trình giảm giá đang hoạt động*/
    @Transactional(readOnly = true)
    public List<DiscountDTO> getActiveDiscounts() {
        Date currentDate = new Date();

        // Tìm các chương trình giảm giá đang hoạt động (thời gian hiện tại nằm trong khoảng từ startDate đến endDate)
        List<Discount> activeDiscounts = discountRepository.findByStartDateBeforeAndEndDateAfter(currentDate, currentDate);

        return activeDiscounts.stream()
                .map(discountMapper::toDiscountDTO)
                .collect(Collectors.toList());
    }

    public Discount getDiscountByCode(String discountCode) {
        return discountRepository.findByDiscountName(discountCode)
                .orElseThrow(() -> new RuntimeException("Discount with name '" + discountCode + "' not found"));
    }

}
