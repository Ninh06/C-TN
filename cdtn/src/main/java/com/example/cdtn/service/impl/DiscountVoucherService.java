package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.discounts.DiscountVoucherDTO;
import com.example.cdtn.dtos.discounts.UpdateDiscountVoucherDTO;
import com.example.cdtn.entity.discounts.DiscountVoucher;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.DiscountMapper;
import com.example.cdtn.repository.DiscountVoucherRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountVoucherService {
    @Autowired
    @Getter 
    private DiscountVoucherRepository discountVoucherRepository;

    @Autowired
    private DiscountMapper discountMapper;

    @Autowired
    public DiscountVoucherService(DiscountVoucherRepository discountVoucherRepository, DiscountMapper discountMapper) {
        this.discountVoucherRepository = discountVoucherRepository;
        this.discountMapper = discountMapper;
    }

    /**Lấy tất cả các voucher giảm giá*/
    @Transactional(readOnly = true)
    public List<DiscountVoucherDTO> getAllVouchers() {
        List<DiscountVoucher> vouchers = discountVoucherRepository.findAll();
        return vouchers.stream()
                .map(discountMapper::toDiscountVoucherDTO)
                .collect(Collectors.toList());
    }

    /**Lấy voucher theo ID*/
    @Transactional(readOnly = true)
    public DiscountVoucherDTO getVoucherById(Long voucherId) {
        DiscountVoucher voucher = discountVoucherRepository.findById(voucherId)
                .orElseThrow(() -> new OurException("Không tìm thấy voucher với ID: " + voucherId));
        return discountMapper.toDiscountVoucherDTO(voucher);
    }

    @Transactional(readOnly = true)
    public DiscountVoucher getVoucherEntityById(Long voucherId) {
        return discountVoucherRepository.findById(voucherId)
                .orElseThrow(() -> new OurException("Không tìm thấy voucher với ID: " + voucherId));

    }

    /**Tìm voucher theo mã code*/
    @Transactional(readOnly = true)
    public DiscountVoucherDTO getVoucherByCode(String voucherCode) {
        DiscountVoucher voucher = discountVoucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new OurException("Không tìm thấy voucher với mã: " + voucherCode));
        return discountMapper.toDiscountVoucherDTO(voucher);
    }

    /**Lấy đối tượng DiscountVoucher theo mã voucher*/
    @Transactional(readOnly = true)
    public DiscountVoucher getVoucherEntityByCode(String voucherCode) {
        return discountVoucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new OurException("Không tìm thấy voucher với mã: " + voucherCode));
    }


    /**Tạo mới một voucher giảm giá*/
    @Transactional
    public DiscountVoucherDTO createVoucher(DiscountVoucherDTO discountVoucherDTO) {
        // Kiểm tra mã voucher đã tồn tại chưa
        if (discountVoucherRepository.existsByVoucherCode(discountVoucherDTO.getVoucherCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại: " + discountVoucherDTO.getVoucherCode());
        }

        DiscountVoucher discountVoucher = discountMapper.toDiscountVoucherEntity(discountVoucherDTO);
        discountVoucher = discountVoucherRepository.save(discountVoucher);
        return discountMapper.toDiscountVoucherDTO(discountVoucher);
    }

    /**Cập nhật thông tin voucher giảm giá*/
    @Transactional
    public DiscountVoucherDTO updateVoucher(Long voucherId, UpdateDiscountVoucherDTO dto) {
        DiscountVoucher existingVoucher = discountVoucherRepository.findById(voucherId)
                .orElseThrow(() -> new OurException("Không tìm thấy voucher với ID: " + voucherId));

        if (dto.getVoucherCode() != null &&
                !dto.getVoucherCode().equals(existingVoucher.getVoucherCode()) &&
                discountVoucherRepository.existsByVoucherCode(dto.getVoucherCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại: " + dto.getVoucherCode());
        }

        discountMapper.updateDiscountVoucherFromDTO(dto, existingVoucher);
        existingVoucher.setUpdatedAt(new Date());

        return discountMapper.toDiscountVoucherDTO(discountVoucherRepository.save(existingVoucher));
    }



    /**Xóa voucher theo ID*/
    @Transactional
    public void deleteVoucher(Long voucherId) {
        if (!discountVoucherRepository.existsById(voucherId)) {
            throw new OurException("Không tìm thấy voucher với ID: " + voucherId);
        }
        discountVoucherRepository.deleteById(voucherId);
    }

    /**Lấy danh sách các voucher có hiệu lực*/
    @Transactional(readOnly = true)
    public List<DiscountVoucherDTO> getActiveVouchers() {
        Date currentDate = new Date();
        List<DiscountVoucher> activeVouchers = discountVoucherRepository.findByStartDateBeforeAndEndDateAfter(
                currentDate, currentDate);
        return activeVouchers.stream()
                .map(discountMapper::toDiscountVoucherDTO)
                .collect(Collectors.toList());
    }

    /**Kiểm tra voucher có hợp lệ không*/
    @Transactional(readOnly = true)
    public boolean isVoucherValid(String voucherCode, Long customerId) {
        try {
            // Tìm voucher theo mã
            DiscountVoucher voucher = discountVoucherRepository.findByVoucherCode(voucherCode)
                    .orElse(null);

            if (voucher == null) {
                return false;
            }

            // Kiểm tra thời hạn
            Date currentDate = new Date();
            if (currentDate.before(voucher.getStartDate()) || currentDate.after(voucher.getEndDate())) {
                return false;
            }

            // Nếu voucher chỉ sử dụng một lần cho mỗi khách hàng
            if (voucher.getOncePerCustomer() && customerId != null) {
                // Kiểm tra xem khách hàng đã sử dụng voucher này chưa
                boolean customerHasUsedVoucher =
                        discountVoucherRepository.hasCustomerUsedVoucher(voucherCode, customerId);
                if (customerHasUsedVoucher) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // Log lỗi nếu có
            e.printStackTrace();
            return false;
        }
    }

    /** Áp dụng voucher vào đơn hàng*/
    @Transactional(readOnly = true)
    public double applyVoucher(String voucherCode, double orderAmount) {
        DiscountVoucher voucher = discountVoucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new OurException("Không tìm thấy voucher với mã: " + voucherCode));

        // Kiểm tra thời hạn
        Date currentDate = new Date();
        if (currentDate.before(voucher.getStartDate()) || currentDate.after(voucher.getEndDate())) {
            throw new IllegalArgumentException("Voucher đã hết hạn hoặc chưa có hiệu lực");
        }

        // Tính số tiền giảm giá
        return orderAmount * (voucher.getDiscountPercentage() / 100);
    }

    public DiscountVoucher saveVoucher(DiscountVoucher voucher) {
        return discountVoucherRepository.save(voucher);
    }
}
