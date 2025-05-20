package com.example.cdtn.dtos.orders;

import com.example.cdtn.dtos.PaymentDTO;
import com.example.cdtn.dtos.discounts.DiscountDetailDTO;
import com.example.cdtn.dtos.discounts.DiscountVoucherDTO;
import com.example.cdtn.dtos.invoices.InvoiceDTO;
import com.example.cdtn.dtos.ships.ShippingAddressDTO;
import com.example.cdtn.dtos.ships.ShippingMethodDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {

    private Long orderId;

    private BuyerDTO buyer;

    private SellerDTO sellerDTO;

    private OrderStatusDTO orderStatus;

    @NotNull(message = "Tổng số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tổng số tiền phải lớn hơn hoặc bằng 0")
    private BigDecimal totalAmount;

    private Date createdAt;

    private DiscountVoucherDTO discountVoucher;

    private ShippingMethodDTO shippingMethod;

    private ShippingAddressDTO shippingAddress;

    private List<OrderItemDTO> orderItems;

    private PaymentDTO payment;

    private List<DiscountDetailDTO> discountDetails;

    private InvoiceDTO invoice;

    private ReturnOrderDTO returnOrder;
}
