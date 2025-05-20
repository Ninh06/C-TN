package com.example.cdtn.mapper;

import com.example.cdtn.dtos.PaymentDTO;
import com.example.cdtn.dtos.discounts.DiscountDetailDTO;
import com.example.cdtn.dtos.discounts.DiscountVoucherDTO;
import com.example.cdtn.dtos.invoices.InvoiceDTO;
import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.dtos.orders.OrderItemDTO;
import com.example.cdtn.dtos.orders.OrderStatusDTO;
import com.example.cdtn.dtos.orders.ReturnOrderDTO;
import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.example.cdtn.dtos.ships.ShippingAddressDTO;
import com.example.cdtn.dtos.ships.ShippingMethodDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.example.cdtn.entity.Payment;
import com.example.cdtn.entity.discounts.DiscountDetail;
import com.example.cdtn.entity.discounts.DiscountVoucher;
import com.example.cdtn.entity.invoices.Invoice;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.orders.OrderItem;
import com.example.cdtn.entity.orders.OrderStatus;
import com.example.cdtn.entity.orders.ReturnOrder;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.example.cdtn.entity.ships.ShippingMethod;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.entity.users.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;

    //Order entity sang dto
    public OrderDTO toOrderDTO(Order order) {
        if(order == null) {
            return null;
        }

        OrderDTO orderDTO = OrderDTO.builder()
                .orderId(order.getOrderId())
                .totalAmount(order.getTotalAmount())
                .build();

        if(order.getBuyer() != null) {
            BuyerDTO buyerDTO = BuyerDTO.builder()
                    .buyerId(order.getBuyer().getBuyerId())
                    .build();
            orderDTO.setBuyer(buyerDTO);
        }
        if(order.getOrderStatus() != null) {
            OrderStatusDTO orderStatusDTO = OrderStatusDTO.builder()
                    .orderStatusId(order.getOrderStatus().getOrderStatusId())
                    .orderStatusDesc(order.getOrderStatus().getOrderStatusDesc())
                    .build();
            orderDTO.setOrderStatus(orderStatusDTO);
        }
        if(order.getVoucher() != null) {
            DiscountVoucherDTO discountVoucherDTO = DiscountVoucherDTO.builder()
                    .voucherId(order.getVoucher().getVoucherId())
                    .voucherCode(order.getVoucher().getVoucherCode())
                    .discountPercentage(order.getVoucher().getDiscountPercentage())
                    .startDate(order.getVoucher().getStartDate())
                    .endDate(order.getVoucher().getEndDate())
                    .quantityVoucher(order.getVoucher().getQuantityVoucher())
                    .oncePerCustomer(order.getVoucher().getOncePerCustomer())
                    .createdAt(order.getVoucher().getCreatedAt())
                    .updatedAt(order.getVoucher().getUpdatedAt())
                    .build();
            orderDTO.setDiscountVoucher(discountVoucherDTO);
        }
        if(order.getShippingMethod() != null) {
            ShippingMethodDTO shippingMethodDTO = ShippingMethodDTO.builder()
                    .id(order.getShippingMethod().getId())
                    .methodName(order.getShippingMethod().getMethodName())
                    .minimumOrderPriceAmount(order.getShippingMethod().getMinimumOrderPriceAmount())
                    .maximumOrderPriceAmount(order.getShippingMethod().getMaximumOrderPriceAmount())
                    .minimumOrderWeight(order.getShippingMethod().getMinimumOrderWeight())
                    .maximumOrderWeight(order.getShippingMethod().getMaximumOrderWeight())
                    .priceAmount(order.getShippingMethod().getPriceAmount())
                    .currency(order.getShippingMethod().getCurrency())
                    .isDefault(order.getShippingMethod().getIsDefault())
                    .build();
            orderDTO.setShippingMethod(shippingMethodDTO);
        }
        if(order.getShippingAddress() != null) {
            ShippingAddressDTO shippingAddressDTO = ShippingAddressDTO.builder()
                    .id(order.getShippingAddress().getId())
                    .firstName(order.getShippingAddress().getFirstName())
                    .lastName(order.getShippingAddress().getLastName())
                    .personName(order.getShippingAddress().getPersonName())
                    .streetAddress1(order.getShippingAddress().getStreetAddress1())
                    .streetAddress2(order.getShippingAddress().getStreetAddress2())
                    .city(order.getShippingAddress().getCity())
                    .postalCode(order.getShippingAddress().getPostalCode())
                    .country(order.getShippingAddress().getCountry())
                    .countryArea(order.getShippingAddress().getCountryArea())
                    .phone(order.getShippingAddress().getPhone())
                    .cityArea(order.getShippingAddress().getCityArea())
                    .build();
            orderDTO.setShippingAddress(shippingAddressDTO);
        }
        if(order.getSeller() != null) {
            SellerDTO sellerDTO =SellerDTO.builder()
                    .sellerId(order.getSeller().getSellerId())
                    .build();
            orderDTO.setSellerDTO(sellerDTO);
        }
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                    .map(orderItem -> OrderItemDTO.builder()
                            .orderItemId(orderItem.getOrderItemId())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPrice())
                            .totalPriceItem(orderItem.getTotalPriceItem())
                            .build())
                    .collect(Collectors.toList());
            orderDTO.setOrderItems(orderItemDTOs);
        } else {
            orderDTO.setOrderItems(new ArrayList<>());
        }
        if(order.getPayment() != null) {
            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .paymentId(order.getPayment().getPaymentId())
                    .paymentMethod(order.getPayment().getPaymentMethod())
                    .paymentDes(order.getPayment().getPaymentDes())
                    .build();
            orderDTO.setPayment(paymentDTO);
        }
        if (order.getDiscountDetails() != null && !order.getDiscountDetails().isEmpty()) {
            List<DiscountDetailDTO> discountDetailDTOs = order.getDiscountDetails().stream()
                    .map(discountDetail -> DiscountDetailDTO.builder()
                            .discountDetailId(discountDetail.getDiscountDetailId())
                            .discountedAmount(discountDetail.getDiscountedAmount())
                            .build())
                    .collect(Collectors.toList());
            orderDTO.setDiscountDetails(discountDetailDTOs);
        } else {
            orderDTO.setDiscountDetails(new ArrayList<>());
        }
        if(order.getInvoice() != null) {
            InvoiceDTO invoiceDTO = InvoiceDTO.builder()
                    .invoiceId(order.getInvoice().getInvoiceId())
                    .totalAmount(order.getInvoice().getTotalAmount())
                    .transactionId(order.getInvoice().getTransactionId())
                    .build();
            orderDTO.setInvoice(invoiceDTO);
        }
        if(order.getReturnOrder() != null) {
            ReturnOrderDTO returnOrderDTO = ReturnOrderDTO.builder()
                    .returnId(order.getReturnOrder().getReturnId())
                    .reason(order.getReturnOrder().getReason())
                    .build();
            orderDTO.setReturnOrder(returnOrderDTO);
        }

        return orderDTO;
    }

    //Order dto sang entity
    public Order toOrderEntity(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }

        Order order = Order.builder()
                .orderId(orderDTO.getOrderId())
                .totalAmount(orderDTO.getTotalAmount())
                .build();

        if (orderDTO.getBuyer() != null) {
            Buyer buyer = Buyer.builder()
                    .buyerId(orderDTO.getBuyer().getBuyerId())
                    .build();
            order.setBuyer(buyer);
        }

        if (orderDTO.getOrderStatus() != null) {
            OrderStatus orderStatus = OrderStatus.builder()
                    .orderStatusId(orderDTO.getOrderStatus().getOrderStatusId())
                    .orderStatusDesc(orderDTO.getOrderStatus().getOrderStatusDesc())
                    .build();
            order.setOrderStatus(orderStatus);
        }

        if (orderDTO.getDiscountVoucher() != null) {
            DiscountVoucher voucher = DiscountVoucher.builder()
                    .voucherId(orderDTO.getDiscountVoucher().getVoucherId())
                    .voucherCode(orderDTO.getDiscountVoucher().getVoucherCode())
                    .discountPercentage(orderDTO.getDiscountVoucher().getDiscountPercentage())
                    .startDate(orderDTO.getDiscountVoucher().getStartDate())
                    .endDate(orderDTO.getDiscountVoucher().getEndDate())
                    .quantityVoucher(orderDTO.getDiscountVoucher().getQuantityVoucher())
                    .oncePerCustomer(orderDTO.getDiscountVoucher().getOncePerCustomer())
                    .createdAt(orderDTO.getDiscountVoucher().getCreatedAt())
                    .updatedAt(orderDTO.getDiscountVoucher().getUpdatedAt())
                    .build();
            order.setVoucher(voucher);
        }

        if (orderDTO.getShippingMethod() != null) {
            ShippingMethod shippingMethod = ShippingMethod.builder()
                    .id(orderDTO.getShippingMethod().getId())
                    .methodName(orderDTO.getShippingMethod().getMethodName())
                    .minimumOrderPriceAmount(orderDTO.getShippingMethod().getMinimumOrderPriceAmount())
                    .maximumOrderPriceAmount(orderDTO.getShippingMethod().getMaximumOrderPriceAmount())
                    .minimumOrderWeight(orderDTO.getShippingMethod().getMinimumOrderWeight())
                    .maximumOrderWeight(orderDTO.getShippingMethod().getMaximumOrderWeight())
                    .priceAmount(orderDTO.getShippingMethod().getPriceAmount())
                    .currency(orderDTO.getShippingMethod().getCurrency())
                    .isDefault(orderDTO.getShippingMethod().getIsDefault())
                    .build();
            order.setShippingMethod(shippingMethod);
        }

        if (orderDTO.getShippingAddress() != null) {
            ShippingAddress shippingAddress = ShippingAddress.builder()
                    .id(orderDTO.getShippingAddress().getId())
                    .firstName(orderDTO.getShippingAddress().getFirstName())
                    .lastName(orderDTO.getShippingAddress().getLastName())
                    .personName(orderDTO.getShippingAddress().getPersonName())
                    .streetAddress1(orderDTO.getShippingAddress().getStreetAddress1())
                    .streetAddress2(orderDTO.getShippingAddress().getStreetAddress2())
                    .city(orderDTO.getShippingAddress().getCity())
                    .postalCode(orderDTO.getShippingAddress().getPostalCode())
                    .country(orderDTO.getShippingAddress().getCountry())
                    .countryArea(orderDTO.getShippingAddress().getCountryArea())
                    .phone(orderDTO.getShippingAddress().getPhone())
                    .cityArea(orderDTO.getShippingAddress().getCityArea())
                    .build();
            order.setShippingAddress(shippingAddress);
        }
        if(orderDTO.getSellerDTO() != null) {
            Seller seller = Seller.builder()
                    .sellerId(orderDTO.getSellerDTO().getSellerId())
                    .build();
            order.setSeller(seller);
        }

        if (orderDTO.getOrderItems() != null && !orderDTO.getOrderItems().isEmpty()) {
            List<OrderItem> orderItems = orderDTO.getOrderItems().stream()
                    .map(orderItemDTO -> OrderItem.builder()
                            .orderItemId(orderItemDTO.getOrderItemId())
                            .quantity(orderItemDTO.getQuantity())
                            .price(orderItemDTO.getPrice())
                            .totalPriceItem(orderItemDTO.getTotalPriceItem())
                            .build())
                    .collect(Collectors.toList());
            order.setOrderItems(orderItems);
        } else {
            order.setOrderItems(new ArrayList<>());
        }

        if (orderDTO.getPayment() != null) {
            Payment payment = Payment.builder()
                    .paymentId(orderDTO.getPayment().getPaymentId())
                    .paymentMethod(orderDTO.getPayment().getPaymentMethod())
                    .paymentDes(orderDTO.getPayment().getPaymentDes())
                    .build();
            order.setPayment(payment);
        }

        if (orderDTO.getDiscountDetails() != null && !orderDTO.getDiscountDetails().isEmpty()) {
            List<DiscountDetail> discountDetails = orderDTO.getDiscountDetails().stream()
                    .map(dto -> DiscountDetail.builder()
                            .discountDetailId(dto.getDiscountDetailId())
                            .discountedAmount(dto.getDiscountedAmount())
                            .build())
                    .collect(Collectors.toList());
            order.setDiscountDetails(discountDetails);
        } else {
            order.setDiscountDetails(new ArrayList<>());
        }

        if (orderDTO.getInvoice() != null) {
            Invoice invoice = Invoice.builder()
                    .invoiceId(orderDTO.getInvoice().getInvoiceId())
                    .totalAmount(orderDTO.getInvoice().getTotalAmount())
                    .transactionId(orderDTO.getInvoice().getTransactionId())
                    .build();
            order.setInvoice(invoice);
        }

        if (orderDTO.getReturnOrder() != null) {
            ReturnOrder returnOrder = ReturnOrder.builder()
                    .returnId(orderDTO.getReturnOrder().getReturnId())
                    .reason(orderDTO.getReturnOrder().getReason())
                    .build();
            order.setReturnOrder(returnOrder);
        }

        return order;
    }


    // Phương thức chuyển đổi từ Entity ReturnOrder sang DTO ReturnOrderDTO
    public ReturnOrderDTO toReturnOrderDTO(ReturnOrder returnOrder) {
        if(returnOrder == null) {
            return null;
        }

        ReturnOrderDTO returnOrderDTO = ReturnOrderDTO.builder()
                .returnId(returnOrder.getReturnId())
                .reason(returnOrder.getReason())
                .build();

        if(returnOrder.getBuyer() != null) {
            BuyerDTO buyerDTO = BuyerDTO.builder()
                    .buyerId(returnOrder.getBuyer().getBuyerId())
                    .build();
            returnOrderDTO.setBuyer(buyerDTO);
        }
        if(returnOrder.getOrder() != null) {
            OrderDTO orderDTO = OrderDTO.builder()
                    .orderId(returnOrder.getOrder().getOrderId())
                    .build();
            returnOrderDTO.setOrder(orderDTO);
        }if(returnOrder.getOrderStatus() != null) {
            OrderStatusDTO orderStatusDTO = OrderStatusDTO.builder()
                    .orderStatusId(returnOrder.getOrderStatus().getOrderStatusId())
                    .orderStatusDesc(returnOrder.getOrderStatus().getOrderStatusDesc())
                    .build();
            returnOrderDTO.setOrderStatus(orderStatusDTO);
        }

        return returnOrderDTO;

    }

    public ReturnOrder toReturnOrderEntity(ReturnOrderDTO returnOrderDTO) {
        if (returnOrderDTO == null) {
            return null;
        }

        ReturnOrder returnOrder = ReturnOrder.builder()
                .returnId(returnOrderDTO.getReturnId())
                .reason(returnOrderDTO.getReason())
                .build();

        if (returnOrderDTO.getBuyer() != null) {
            Buyer buyer = Buyer.builder()
                    .buyerId(returnOrderDTO.getBuyer().getBuyerId())
                    .build();
            returnOrder.setBuyer(buyer);
        }

        if (returnOrderDTO.getOrder() != null) {
            Order order = Order.builder()
                    .orderId(returnOrderDTO.getOrder().getOrderId())
                    .build();
            returnOrder.setOrder(order);
        }

        if (returnOrderDTO.getOrderStatus() != null) {
            OrderStatus orderStatus = OrderStatus.builder()
                    .orderStatusId(returnOrderDTO.getOrderStatus().getOrderStatusId())
                    .orderStatusDesc(returnOrderDTO.getOrderStatus().getOrderStatusDesc())
                    .build();
            returnOrder.setOrderStatus(orderStatus);
        }

        return returnOrder;
    }


    // Phương thức chuyển đổi từ Entity OrderStatus sang DTO OrderStatusDTO
    public OrderStatusDTO toOrderStatusDTO(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }

        OrderStatusDTO orderStatusDTO = OrderStatusDTO.builder()
                .orderStatusId(orderStatus.getOrderStatusId())
                .orderStatusDesc(orderStatus.getOrderStatusDesc())
                .createdAt(orderStatus.getCreatedAt())
                .build();

        if(orderStatus.getOrders() != null && !orderStatus.getOrders().isEmpty()) {
            List<OrderDTO> orderDTOs = orderStatus.getOrders().stream()
                    .map(order -> OrderDTO.builder()
                            .orderId(order.getOrderId())
                            .build())
                    .collect(Collectors.toList());
            orderStatusDTO.setOrders(orderDTOs);
        } else {
            orderStatusDTO.setOrders(new ArrayList<>());
        }

        if(orderStatus.getReturnOrders() != null && !orderStatus.getReturnOrders().isEmpty()) {
            List<ReturnOrderDTO> returnOrderDTOS = orderStatus.getReturnOrders().stream()
                    .map(returnOrder -> ReturnOrderDTO.builder()
                            .returnId(returnOrder.getReturnId())
                            .reason(returnOrder.getReason())
                            .build())
                    .collect(Collectors.toList());
            orderStatusDTO.setReturnOrders(returnOrderDTOS);
        } else {
            orderStatusDTO.setOrders(new ArrayList<>());
        }

        return orderStatusDTO;
    }

    // Phương thức chuyển đổi từ DTO OrderStatusDTO sang Entity OrderStatus
    public OrderStatus toOrderStatus(OrderStatusDTO orderStatusDTO) {
        if (orderStatusDTO == null) {
            return null;
        }

        OrderStatus orderStatus = OrderStatus.builder()
                .orderStatusId(orderStatusDTO.getOrderStatusId())
                .orderStatusDesc(orderStatusDTO.getOrderStatusDesc())
                .createdAt(orderStatusDTO.getCreatedAt())
                .build();

        // Chỉ ánh xạ danh sách Order với thông tin cơ bản (tránh vòng lặp)
        if (orderStatusDTO.getOrders() != null && !orderStatusDTO.getOrders().isEmpty()) {
            List<Order> orders = orderStatusDTO.getOrders().stream()
                    .map(orderDTO -> {
                        Order order = new Order();
                        order.setOrderId(orderDTO.getOrderId());
                        // Không set OrderStatus hay orderItems để tránh lặp
                        return order;
                    })
                    .collect(Collectors.toList());
            orderStatus.setOrders(orders);
        } else {
            orderStatus.setOrders(new ArrayList<>());
        }

        // Ánh xạ danh sách ReturnOrder với thông tin cơ bản
        if (orderStatusDTO.getReturnOrders() != null && !orderStatusDTO.getReturnOrders().isEmpty()) {
            List<ReturnOrder> returnOrders = orderStatusDTO.getReturnOrders().stream()
                    .map(returnOrderDTO -> {
                        return ReturnOrder.builder()
                                .returnId(returnOrderDTO.getReturnId())
                                .reason(returnOrderDTO.getReason())
                                .build();
                    })
                    .collect(Collectors.toList());
            orderStatus.setReturnOrders(returnOrders);
        } else {
            orderStatus.setReturnOrders(new ArrayList<>());
        }

        return orderStatus;
    }



    // Entity -> DTO
    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDTO dto = OrderItemDTO.builder()
                .orderItemId(orderItem.getOrderItemId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .createdAt(orderItem.getCreatedAt())
                .build();

        // Chỉ set thông tin cơ bản của Order để tránh vòng lặp vô hạn
        if (orderItem.getOrder() != null) {
            OrderDTO orderDTO = OrderDTO.builder()
                    .orderId(orderItem.getOrder().getOrderId())
                    .build();
            dto.setOrder(orderDTO);
        }

        // Sử dụng ProductVariantMapper để chuyển đổi
        if (orderItem.getProductVariant() != null) {
            ProductVariantDTO productVariantDTO = ProductVariantDTO.builder()
                    .id(orderItem.getProductVariant().getId())
                    .name(orderItem.getProductVariant().getName())
                    .price(orderItem.getProductVariant().getPrice())
                    .quantity(orderItem.getProductVariant().getQuantity())
                    .build();
            dto.setProductVariant(productVariantDTO);
        }
        if(orderItem.getProduct() != null) {
            ProductDTO productDTO = ProductDTO.builder()
                    .productId(orderItem.getProduct().getProductId())
                    .name(orderItem.getProduct().getName())
                    .title(orderItem.getProduct().getTitle())
                    .description(orderItem.getProduct().getDescription())
                    .weight(orderItem.getProduct().getWeight())
                    .currency(orderItem.getProduct().getCurrency())
                    .minimalVariantPriceAmount(orderItem.getProduct().getMinimalVariantPriceAmount())
                    .minimalQuantity(orderItem.getProduct().getMinimalQuantity())
                    .availableForPurchase(orderItem.getProduct().getAvailableForPurchase())
                    .build();
            dto.setProductDTO(productDTO);
        }

        return dto;
    }

    // Chuyển từ DTO → Entity
    public OrderItem toOrderItemEntity(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }

        OrderItem entity = OrderItem.builder()
                .orderItemId(dto.getOrderItemId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .createdAt(dto.getCreatedAt())
                .build();

        // Chỉ ánh xạ ID của Order để không load toàn bộ orderItems
        if (dto.getOrder() != null) {
            Order order = Order.builder()
                    .orderId(dto.getOrder().getOrderId())
                    .build();
            entity.setOrder(order);
        }

        // Sử dụng ProductVariantMapper để chuyển đổi ngược
        if (dto.getProductVariant() != null) {
            ProductVariant productVariant = ProductVariant.builder()
                    .id(dto.getProductVariant().getId())
                    .name(dto.getProductVariant().getName())
                    .price(dto.getProductVariant().getPrice())
                    .quantity(dto.getProductVariant().getQuantity())
                    .build();
            entity.setProductVariant(productVariant);
        }

        if(dto.getProductDTO() != null) {
            Product product = Product.builder()
                    .productId(dto.getProductDTO().getProductId())
                    .name(dto.getProductDTO().getName())
                    .title(dto.getProductDTO().getTitle())
                    .description(dto.getProductDTO().getDescription())
                    .weight(dto.getProductDTO().getWeight())
                    .currency(dto.getProductDTO().getCurrency())
                    .minimalVariantPriceAmount(dto.getProductDTO().getMinimalVariantPriceAmount())
                    .minimalQuantity(dto.getProductDTO().getMinimalQuantity())
                    .availableForPurchase(dto.getProductDTO().getAvailableForPurchase())
                    .build();
            entity.setProduct(product);
        }

        return entity;
    }
}
