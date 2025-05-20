package com.example.cdtn.mapper;

import com.example.cdtn.dtos.PaymentDTO;
import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.example.cdtn.entity.Payment;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.users.Buyer;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentDTO toPaymentDTO(Payment payment) {
        if(payment == null) {
            return null;
        }

        PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDes(payment.getPaymentDes())
                .build();


        return paymentDTO;
    }

    public Payment toPaymentEntity(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return null;
        }

        Payment payment = Payment.builder()
                .paymentId(paymentDTO.getPaymentId())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .paymentDes(paymentDTO.getPaymentDes())
                .build();


        return payment;
    }


}
