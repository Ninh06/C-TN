package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.invoices.InvoiceStatusDTO;
import com.example.cdtn.entity.invoices.InvoiceStatus;
import com.example.cdtn.mapper.InvoiceMapper;
import com.example.cdtn.repository.InvoiceStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceStatusService {

    private final InvoiceStatusRepository invoiceStatusRepository;
    private final InvoiceMapper invoiceMapper;

    // Lấy tất cả InvoiceStatus dưới dạng DTO
    public List<InvoiceStatusDTO> getAllInvoiceStatuses() {
        List<InvoiceStatus> invoiceStatuses = invoiceStatusRepository.findAll();
        return invoiceStatuses.stream()
                .map(invoiceMapper::toInvoiceStatusDTO)
                .collect(Collectors.toList());
    }

    // Lấy một InvoiceStatus theo ID (trả về DTO)
    public InvoiceStatusDTO getInvoiceStatusById(Long invoiceStatusId) {
        InvoiceStatus invoiceStatus = invoiceStatusRepository.findById(invoiceStatusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái hóa đơn với ID: " + invoiceStatusId));
        return invoiceMapper.toInvoiceStatusDTO(invoiceStatus);
    }

    // Tạo mới InvoiceStatus từ DTO
    @Transactional
    public InvoiceStatusDTO createInvoiceStatus(InvoiceStatusDTO invoiceStatusDTO) {
        InvoiceStatus invoiceStatus = invoiceMapper.toInvoiceStatusEntity(invoiceStatusDTO);
        InvoiceStatus saved = invoiceStatusRepository.save(invoiceStatus);
        return invoiceMapper.toInvoiceStatusDTO(saved);
    }

    // Cập nhật InvoiceStatus từ DTO
    @Transactional
    public InvoiceStatusDTO updateInvoiceStatus(Long invoiceStatusId, InvoiceStatusDTO invoiceStatusDTO) {
        InvoiceStatus existing = invoiceStatusRepository.findById(invoiceStatusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái hóa đơn với ID: " + invoiceStatusId));

        // Cập nhật các trường
        existing.setInvoiceStatusDesc(invoiceStatusDTO.getInvoiceStatusDesc());

        InvoiceStatus updated = invoiceStatusRepository.save(existing);
        return invoiceMapper.toInvoiceStatusDTO(updated);
    }

    // Xóa InvoiceStatus theo ID
    @Transactional
    public void deleteInvoiceStatus(Long invoiceStatusId) {
        InvoiceStatus invoiceStatus = invoiceStatusRepository.findById(invoiceStatusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái hóa đơn với ID: " + invoiceStatusId));
        invoiceStatusRepository.delete(invoiceStatus);
    }

    public InvoiceStatus getInvoiceStatusEntityById(Long invoiceStatusId) {
        return invoiceStatusRepository.findById(invoiceStatusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái hóa đơn với ID: " + invoiceStatusId));
    }
}
