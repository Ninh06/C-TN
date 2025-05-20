package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.PaymentDTO;
import com.example.cdtn.entity.Payment;
import com.example.cdtn.mapper.PaymentMapper;
import com.example.cdtn.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toPaymentEntity(paymentDTO);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toPaymentDTO(savedPayment);
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phương thức thanh toán với ID là: " + paymentId));
        return paymentMapper.toPaymentDTO(payment);
    }

    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public void deletePayment(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new RuntimeException("Không tìm thấy phương thức thanh toán với ID là: " + paymentId);
        }
        paymentRepository.deleteById(paymentId);
    }
}
