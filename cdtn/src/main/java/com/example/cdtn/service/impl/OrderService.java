package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.Payment;
import com.example.cdtn.entity.discounts.Discount;
import com.example.cdtn.entity.discounts.DiscountDetail;
import com.example.cdtn.entity.discounts.DiscountVoucher;
import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.flashsale.ProductVariantFlashSale;
import com.example.cdtn.entity.invoices.Invoice;
import com.example.cdtn.entity.invoices.InvoiceStatus;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.orders.OrderItem;
import com.example.cdtn.entity.orders.OrderStatus;
import com.example.cdtn.entity.orders.ReturnOrder;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.ships.ShippingAddress;
import com.example.cdtn.entity.ships.ShippingMethod;
import com.example.cdtn.entity.ships.ShippingZone;
import com.example.cdtn.entity.shopcart.CartItem;
import com.example.cdtn.entity.shopcart.ShoppingCart;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.mapper.OrderMapper;
import com.example.cdtn.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ShippingMethodService shippingMethodService;

    @Autowired
    private DiscountVoucherService discountVoucherService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private DiscountDetailRepository discountDetailRepository;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private ShippingAddressService shippingAddressService;
    @Autowired
    private ShippingZoneRepository shippingZoneRepository;
    @Autowired
    private BuyerService buyerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private ReturnOrderRepository returnOrderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InvoiceStatusRepository invoiceStatusRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ProductVariantFlashSaleRepository productVariantFlashSaleRepository;
    @Autowired
    private ProductFlashSaleRepository productFlashSaleRepository;

    @Transactional
    public List<OrderDTO> createOrdersFromCart(Long buyerId, Long shippingAddressId, Long voucherId, Long discountId, Long shippingMethodId) {
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartEntityByBuyerId(buyerId);
        if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống, không thể tạo đơn hàng.");
        }

        // Cập nhật giá của CartItem nếu flash sale không còn hiệu lực
        shoppingCartService.updateCartItemPrices(shoppingCart);

        ShippingAddress shippingAddress = shippingAddressService.getShippingAddressById(shippingAddressId);

        // Lấy phương thức thanh toán mặc định với ID = 9
        Payment defaultPayment = paymentRepository.findById(9L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán mặc định với ID = 9"));

        // Nhóm các mục trong giỏ hàng theo seller
        Map<Seller, List<CartItem>> cartItemsBySeller = shoppingCart.getCartItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller()));

        List<Order> createdOrders = new ArrayList<>();

        // Lấy phương thức vận chuyển đã chọn (nếu có)
        ShippingMethod selectedShippingMethod = null;
        if (shippingMethodId != null) {
            selectedShippingMethod = shippingMethodService.getShippingMethodEntityById(shippingMethodId);
        }

        // Lấy thông tin voucher và discount
        DiscountVoucher voucher = null;
        if (voucherId != null) {
            voucher = discountVoucherService.getVoucherEntityById(voucherId);
            validateVoucher(voucher, buyerService.getBuyerById(buyerId));
        }

        Discount discount = null;
        if (discountId != null) {
            discount = discountService.getDiscountEntityById(discountId);
        }

        // Kiểm tra khu vực của buyer
        boolean isBuyerInVietnam = "Việt Nam".equalsIgnoreCase(shippingAddress.getCountry());

        // Lấy trạng thái hóa đơn "Chưa thanh toán" (ID = 1)
        InvoiceStatus unpaidStatus = invoiceStatusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái hóa đơn với ID = 1"));

        // Tạo đơn hàng riêng cho từng seller
        for (Map.Entry<Seller, List<CartItem>> entry : cartItemsBySeller.entrySet()) {
            Seller seller = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();

            Order order = new Order();
            order.setBuyer(shoppingCart.getBuyer());
            order.setSeller(seller);
            order.setShippingAddress(shippingAddress);
            order.setOrderStatus(getOrderStatusById(1L));
            order.setPayment(defaultPayment);

            // Tạo các OrderItem từ CartItem
            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CartItem cartItem : sellerItems) {
                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .productVariant(cartItem.getProductVariant())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getUnitPrice())
                        .totalPriceItem(cartItem.getTotalPriceItem())
                        .build();
                orderItems.add(orderItem);
                totalAmount = totalAmount.add(orderItem.getTotalPriceItem());
            }

            order.setOrderItems(orderItems);
            order.setTotalAmount(totalAmount);

            // Xác định vùng vận chuyển cho đơn hàng này
            ShippingZone shippingZone = determineShippingZone(shippingAddress, List.of(seller.getAddress()));
            boolean isSellerInVietnam = "Việt Nam".equalsIgnoreCase(seller.getAddress().getCountry());

            // XỬ LÝ PHƯƠNG THỨC VẬN CHUYỂN
            if (isBuyerInVietnam && isSellerInVietnam) {
                if (selectedShippingMethod != null && "Việt Nam".equals(selectedShippingMethod.getShippingZone().getZoneName())) {
                    String validationError = validateShippingMethod(order, selectedShippingMethod);
                    if (validationError != null) {
                        throw new IllegalArgumentException(validationError);
                    }
                    order.setShippingMethod(selectedShippingMethod);
                } else {
                    ShippingMethod defaultMethod = getDefaultShippingMethodForZone(
                            shippingZoneRepository.findByZoneName("Việt Nam")
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực 'Việt Nam'"))
                    );
                    order.setShippingMethod(defaultMethod);
                }
            } else {
                ShippingMethod internationalMethod = getDefaultShippingMethodForZone(
                        shippingZoneRepository.findByZoneName("Nước Ngoài")
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực 'Nước Ngoài'"))
                );
                order.setShippingMethod(internationalMethod);
            }

            // Thêm phí vận chuyển vào tổng tiền đơn hàng
            if (order.getShippingMethod() != null && order.getShippingMethod().getPriceAmount() != null) {
                BigDecimal shippingCost = BigDecimal.valueOf(order.getShippingMethod().getPriceAmount());
                order.setTotalAmount(order.getTotalAmount().add(shippingCost));
            }

            // Lưu order trước khi áp dụng discount
            Order savedOrder = orderRepository.save(order);

            // Áp dụng khuyến mãi và voucher
            applyDiscountsToOrder(savedOrder, voucher, discount, cartItemsBySeller.size());

            // Tạo invoice cho đơn hàng với trạng thái "Chưa thanh toán"
            createInvoiceForOrder(savedOrder, unpaidStatus);

            // Cập nhật số lượng tồn kho
            updateInventory(orderItems);

            createdOrders.add(savedOrder);
        }

        // Xóa giỏ hàng sau khi đã xử lý tất cả đơn hàng
        shoppingCartService.clearShoppingCart(buyerId);

        // Chuyển đổi các đơn hàng thành DTO
        return createdOrders.stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    /**Tạo đơn hàng mới trực tiếp (không thông qua giỏ hàng)*/
    @Transactional
    public OrderDTO createOrder(Long buyerId, Long sellerId, Long shippingAddressId, Long shippingMethodId,
                                Long voucherId, Long discountId, List<Map<String, Object>> orderItems) {
        Buyer buyer = buyerService.getBuyerById(buyerId);
        Seller seller = sellerService.getSellerById(sellerId);
        ShippingAddress shippingAddress = shippingAddressService.getShippingAddressById(shippingAddressId);
        ShippingMethod shippingMethod = shippingMethodService.getShippingMethodEntityById(shippingMethodId);
        Payment defaultPayment = paymentRepository.findById(9L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán mặc định với ID = 9"));

        // Lấy thông tin voucher và discount nếu có
        DiscountVoucher voucher = null;
        if (voucherId != null) {
            voucher = discountVoucherService.getVoucherEntityById(voucherId);
            validateVoucher(voucher, buyer);
        }

        Discount discount = null;
        if (discountId != null) {
            discount = discountService.getDiscountEntityById(discountId);
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setShippingAddress(shippingAddress);
        order.setOrderStatus(getOrderStatusById(1L));
        order.setPayment(defaultPayment);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Tạo các OrderItem từ dữ liệu đầu vào
        for (Map<String, Object> itemData : orderItems) {
            OrderItem item = createOrderItemFromData(order, itemData);
            items.add(item);
            totalAmount = totalAmount.add(item.getTotalPriceItem());
        }

        order.setOrderItems(items);
        order.setTotalAmount(totalAmount);

        // Xác thực phương thức vận chuyển
        String validationError = validateShippingMethod(order, shippingMethod);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        order.setShippingMethod(shippingMethod);

        // Thêm phí vận chuyển vào tổng tiền
        if (shippingMethod.getPriceAmount() != null) {
            BigDecimal shippingCost = BigDecimal.valueOf(shippingMethod.getPriceAmount());
            order.setTotalAmount(order.getTotalAmount().add(shippingCost));
        }

        // Lưu đơn hàng trước khi áp dụng discount
        Order savedOrder = orderRepository.save(order);

        // Áp dụng khuyến mãi và voucher
        applyDiscountsToOrder(savedOrder, voucher, discount, 1); // Số lượng seller là 1 vì đây là đơn hàng trực tiếp

        // Lấy trạng thái hóa đơn "Chưa thanh toán" (ID = 1)
        InvoiceStatus unpaidStatus = invoiceStatusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái hóa đơn với ID = 1"));

        // Tạo invoice cho đơn hàng với trạng thái "Chưa thanh toán"
        createInvoiceForOrder(savedOrder, unpaidStatus);

        // Cập nhật tồn kho
        updateInventory(items);

        return orderMapper.toOrderDTO(savedOrder);
    }

    /**Tạo OrderItem từ dữ liệu đầu vào*/
    private OrderItem createOrderItemFromData(Order order, Map<String, Object> itemData) {
        OrderItem item = new OrderItem();
        item.setOrder(order);

        Long productId = ((Number) itemData.get("productId")).longValue();
        Long variantId = itemData.get("variantId") != null ? ((Number) itemData.get("variantId")).longValue() : null;
        Integer quantity = ((Number) itemData.get("quantity")).intValue();

        Product product = productService.getProductEntityById(productId);
        ProductVariant variant = variantId != null ? productVariantService.getVariantById(variantId) : null;

        item.setProduct(product);

        ProductFlashSale activeProductFlashSale = null;
        ProductVariantFlashSale activeVariantFlashSale = null;

        if (variant != null) {
            item.setProductVariant(variant);

            // Tìm tất cả ProductVariantFlashSale liên quan đến biến thể và kiểm tra trạng thái
            List<ProductVariantFlashSale> variantFlashSales = productVariantFlashSaleRepository
                    .findByProductVariantIdAndIsActiveTrue(variant.getId());

            activeVariantFlashSale = variantFlashSales.stream()
                    .filter(vfs -> vfs.getProductFlashSale().getFlashSale().getIsActive())
                    .findFirst()
                    .orElse(null);

            if (activeVariantFlashSale != null) {
                // Kiểm tra quota của ProductFlashSale tương ứng
                activeProductFlashSale = activeVariantFlashSale.getProductFlashSale();
                validateAndUpdateQuota(activeProductFlashSale, quantity);

                // Sử dụng giá flash sale
                item.setPrice(BigDecimal.valueOf(activeVariantFlashSale.getFlashSalePrice()));
            } else {
                // Sử dụng giá thông thường
                item.setPrice(variant.getPrice());
            }
        } else {
            // Trường hợp không có biến thể, tìm tất cả ProductFlashSale liên quan đến sản phẩm
            List<ProductFlashSale> productFlashSales = product.getProductFlashSales() != null
                    ? product.getProductFlashSales().stream()
                    .filter(pfs -> pfs.getIsActive() && pfs.getFlashSale().getIsActive())
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            activeProductFlashSale = productFlashSales.stream()
                    .findFirst()
                    .orElse(null);

            if (activeProductFlashSale != null) {
                // Kiểm tra quota của ProductFlashSale
                validateAndUpdateQuota(activeProductFlashSale, quantity);

                // Sử dụng giá flash sale
                item.setPrice(BigDecimal.valueOf(activeProductFlashSale.getFlashSalePrice()));
            } else {
                // Sử dụng giá thông thường
                item.setPrice(BigDecimal.valueOf(product.getMinimalVariantPriceAmount()));
            }
        }

        item.setQuantity(quantity);
        item.setTotalPriceItem(item.getPrice().multiply(BigDecimal.valueOf(quantity)));

        // Lưu ProductFlashSale nếu có cập nhật quota
        if (activeProductFlashSale != null) {
            productFlashSaleRepository.save(activeProductFlashSale);
        }

        return item;
    }

    /**Kiểm tra và cập nhật quota của ProductFlashSale*/
    private void validateAndUpdateQuota(ProductFlashSale productFlashSale, Integer requestedQuantity) {
        Integer quota = productFlashSale.getQuota();
        Integer soldCount = productFlashSale.getSoldCount() != null ? productFlashSale.getSoldCount() : 0;

        if (quota == null || quota <= 0) {
            throw new IllegalArgumentException("Số lượng quota của flash sale không hợp lệ cho sản phẩm ID: " +
                    productFlashSale.getProduct().getProductId());
        }

        int availableQuota = quota - soldCount;
        if (requestedQuantity > availableQuota) {
            throw new IllegalArgumentException("Số lượng yêu cầu (" + requestedQuantity +
                    ") vượt quá quota còn lại (" + availableQuota + ") cho sản phẩm flash sale ID: " +
                    productFlashSale.getId());
        }

        // Cập nhật soldCount
        productFlashSale.setSoldCount(soldCount + requestedQuantity);

        // Nếu quota bằng soldCount, đặt isActive = false
        if (productFlashSale.getSoldCount() >= quota) {
            productFlashSale.setIsActive(false);
            // Cập nhật isActive của các ProductVariantFlashSale liên quan
            List<ProductVariantFlashSale> variantFlashSales = productVariantFlashSaleRepository
                    .findByProductFlashSaleId(productFlashSale.getId());
            for (ProductVariantFlashSale variantFlashSale : variantFlashSales) {
                variantFlashSale.setIsActive(false);
                productVariantFlashSaleRepository.save(variantFlashSale);
            }
        }
    }

    /**Lấy danh sách đơn hàng theo ID của buyer*/
    public List<OrderDTO> getOrdersByBuyerId(Long buyerId) {
        Buyer buyer = buyerService.getBuyerById(buyerId);
        List<Order> orders = orderRepository.findByBuyerOrderByCreatedAtDesc(buyer);
        return orders.stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    /**Lấy danh sách đơn hàng theo ID của seller*/
    public List<OrderDTO> getOrdersBySellerId(Long sellerId) {
        Seller seller = sellerService.getSellerById(sellerId);
        List<Order> orders = orderRepository.findBySellerOrderByCreatedAtDesc(seller);
        return orders.stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    /**Lấy chi tiết đơn hàng theo ID*/
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        return orderMapper.toOrderDTO(order);
    }

    /**Cập nhật trạng thái đơn hàng*/
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Long statusId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Xử lý trường hợp hủy đơn hàng (tích hợp từ cancelOrder)
        if (statusId == 5L) { // Trạng thái "Đã hủy"
            // Kiểm tra nếu đơn hàng có thể hủy
            if (order.getOrderStatus().getOrderStatusId() > 2L) { // Không thể hủy đơn khi đã qua trạng thái xử lý
                throw new IllegalStateException("Không thể hủy đơn hàng ở trạng thái hiện tại");
            }

            // Kiểm tra lý do hủy đơn
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Lý do hủy đơn không được để trống");
            }
        }

        // Xử lý trạng thái trả hàng
        if (statusId == 6L) {
            // Kiểm tra lý do trả hàng
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Lý do trả hàng không được để trống");
            }

            // Kiểm tra nếu đơn hàng đã tồn tại yêu cầu trả hàng
            if (returnOrderRepository.existsByOrderOrderId(orderId)) {
                throw new IllegalStateException("Đơn hàng này đã có yêu cầu trả hàng");
            }

            // Kiểm tra điều kiện đơn hàng có thể trả (đã giao)
            if (order.getOrderStatus().getOrderStatusId() != 4L) {
                throw new IllegalStateException("Chỉ có thể trả hàng khi đơn hàng đã giao");
            }
        }

        // Cập nhật trạng thái đơn hàng
        OrderStatus newStatus = getOrderStatusById(statusId);
        order.setOrderStatus(newStatus);

        // Xử lý theo trạng thái mới
        switch (statusId.intValue()) {
            case 4: // Đã giao hàng
                updateInvoiceStatus(order, 2L); // Cập nhật hóa đơn thành "Đã thanh toán"
                break;

            case 5: // Đã hủy
                restoreInventory(order); // Khôi phục tồn kho
                updateInvoiceStatus(order, 5L); // Cập nhật hóa đơn thành "Hoàn tiền"
                break;

            case 6: // Trả hàng
                restoreInventory(order); // Khôi phục tồn kho
                createReturnOrder(order, reason);
                updateInvoiceStatus(order, 4L); // Cập nhật hóa đơn thành "Hoàn tiền"
                break;
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(updatedOrder);
    }

    /**Tạo đơn trả hàng mới*/
    private ReturnOrder createReturnOrder(Order order, String reason) {
        ReturnOrder returnOrder = ReturnOrder.builder()
                .buyer(order.getBuyer())
                .order(order)
                .reason(reason)
                .orderStatus(getOrderStatusById(6L)) // Trạng thái "Yêu cầu trả hàng"
                .build();

        return returnOrderRepository.save(returnOrder);
    }


    /**
     * Khôi phục tồn kho khi đơn hàng bị hủy
     */
    private void restoreInventory(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            if (item.getProductVariant() != null) {
                var variant = item.getProductVariant();
                variant.setQuantity(variant.getQuantity() + item.getQuantity());
            } else if (item.getProduct() != null) {
                var product = item.getProduct();
                product.setMinimalQuantity(product.getMinimalQuantity() + item.getQuantity());
            }
        }
    }

    /**Khôi phục tồn kho khi đơn hàng bị hủy*/
    private void restoreInventory(Order order) {
        restoreInventory(order.getOrderItems());
    }

    /**Lấy danh sách đơn hàng theo trạng thái*/
    public List<OrderDTO> getOrdersByStatus(Long statusId) {
        OrderStatus status = getOrderStatusById(statusId);
        List<Order> orders = orderRepository.findByOrderStatus(status);
        return orders.stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    /**Lọc đơn hàng theo nhiều tiêu chí*/
    public List<OrderDTO> filterOrders(Long buyerId, Long sellerId, Long statusId, Date fromDate, Date toDate) {
        // Giả định sẽ có phương thức custom trong repository hoặc sử dụng Specification
        List<Order> filteredOrders = orderRepository.findByFilters(buyerId, sellerId, statusId, fromDate, toDate);
        return filteredOrders.stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    /** Cập nhật thông tin giao hàng cho đơn hàng */
    @Transactional
    public OrderDTO updateOrderShipping(Long orderId, Long shippingAddressId, Long shippingMethodId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra trạng thái đơn hàng
        if (order.getOrderStatus().getOrderStatusId() > 1L) {
            throw new IllegalStateException("Không thể cập nhật thông tin giao hàng ở trạng thái hiện tại");
        }

        // Cập nhật địa chỉ giao hàng nếu có
        if (shippingAddressId != null) {
            ShippingAddress newAddress = shippingAddressService.getShippingAddressById(shippingAddressId);
            order.setShippingAddress(newAddress);
        }

        // Cập nhật phương thức giao hàng nếu có
        if (shippingMethodId != null) {
            ShippingMethod newMethod = shippingMethodService.getShippingMethodEntityById(shippingMethodId);

            // Xác thực phương thức vận chuyển mới với địa chỉ đã cập nhật
            String validationError = validateShippingMethod(order, newMethod);
            if (validationError != null) {
                throw new IllegalArgumentException(validationError);
            }

            // Trừ phí vận chuyển cũ nếu có
            if (order.getShippingMethod() != null && order.getShippingMethod().getPriceAmount() != null) {
                BigDecimal oldShippingCost = BigDecimal.valueOf(order.getShippingMethod().getPriceAmount());
                order.setTotalAmount(order.getTotalAmount().subtract(oldShippingCost));
            }

            // Gán phương thức giao hàng mới
            order.setShippingMethod(newMethod);
            if (newMethod.getPriceAmount() != null) {
                BigDecimal newShippingCost = BigDecimal.valueOf(newMethod.getPriceAmount());
                order.setTotalAmount(order.getTotalAmount().add(newShippingCost));
            }
        }

        // Nếu chỉ cập nhật địa chỉ, cần xác minh phương thức giao hàng hiện tại vẫn hợp lệ
        else if (shippingAddressId != null && order.getShippingMethod() != null) {
            String validationError = validateShippingMethod(order, order.getShippingMethod());
            if (validationError != null) {
                throw new IllegalArgumentException(validationError);
            }
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderDTO(updatedOrder);
    }



    private void validateVoucher(DiscountVoucher voucher, Buyer buyer) {
        if (voucher.getQuantityVoucher() <= 0) {
            throw new RuntimeException("Voucher đã hết lượt sử dụng");
        }

        // Kiểm tra nếu voucher chỉ được dùng 1 lần bởi mỗi khách hàng
        if (voucher.getOncePerCustomer()) {
            boolean alreadyUsed = orderRepository.existsByBuyerAndVoucher(buyer, voucher);
            if (alreadyUsed) {
                throw new RuntimeException("Bạn đã sử dụng voucher này rồi");
            }
        }

        if (!isWithinDateRange(voucher.getStartDate(), voucher.getEndDate())) {
            throw new RuntimeException("Voucher đã hết hạn hoặc chưa đến thời gian sử dụng");
        }
    }

    private ShippingZone determineShippingZone(ShippingAddress buyerAddress, List<Address> sellerAddresses) {
        for (Address sellerAddr : sellerAddresses) {
            if (!"Việt Nam".equalsIgnoreCase(sellerAddr.getCountry()) ||
                    !"Việt Nam".equalsIgnoreCase(buyerAddress.getCountry())) {
                return shippingZoneRepository.findByZoneName("Nước Ngoài")
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực 'Nước Ngoài'"));
            }
        }
        return shippingZoneRepository.findByZoneName("Việt Nam")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực 'Việt Nam'"));
    }

    // Phương thức mới để lấy phương thức vận chuyển mặc định của một vùng vận chuyển
    private ShippingMethod getDefaultShippingMethodForZone(ShippingZone shippingZone) {
        return shippingZone.getShippingMethods().stream()
                .filter(method -> Boolean.TRUE.equals(method.getIsDefault()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Không tìm thấy phương thức vận chuyển mặc định cho khu vực: "
                                + shippingZone.getZoneName()));
    }

    private OrderStatus getOrderStatusById(Long orderStatusId) {
        return orderStatusRepository.findById(orderStatusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái đơn hàng với Id: " + orderStatusId));
    }

    private boolean isWithinDateRange(Date start, Date end) {
        Date now = new Date();
        return now.after(start) && now.before(end);
    }

    private String   generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }

    private void applyDiscountsToOrder(Order order, DiscountVoucher voucher, Discount discount, int totalOrders) {
        // Áp dụng voucher
        if (voucher != null) {
            BigDecimal voucherDiscount = order.getTotalAmount()
                    .multiply(BigDecimal.valueOf(voucher.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP);

            // Cập nhật tổng tiền đơn hàng sau khi áp dụng voucher
            order.setTotalAmount(order.getTotalAmount().subtract(voucherDiscount));

            // Lưu order trước khi tạo DiscountDetail
            order = orderRepository.save(order);

            // Tạo và lưu thông tin chi tiết discount
            discountDetailRepository.save(DiscountDetail.builder()
                    .voucher(voucher)
                    .order(order)
                    .discountedAmount(voucherDiscount.doubleValue())
                    .build());

            // Giảm số lượng voucher còn lại
            voucher.setQuantityVoucher(voucher.getQuantityVoucher() - 1);
            discountVoucherService.saveVoucher(voucher);
        }

        // Áp dụng discount
        if (discount != null && isWithinDateRange(discount.getStartDate(), discount.getEndDate())) {
            BigDecimal orderAmount = order.getTotalAmount();
            if (discount.getMinOrderValue() == null || orderAmount.compareTo(discount.getMinOrderValue()) >= 0) {
                BigDecimal discountAmount = orderAmount
                        .multiply(discount.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP);

                // Cập nhật tổng tiền đơn hàng sau khi áp dụng discount
                order.setTotalAmount(orderAmount.subtract(discountAmount));

                // Lưu order trước khi tạo DiscountDetail
                order = orderRepository.save(order);

                // Tạo và lưu thông tin chi tiết discount
                discountDetailRepository.save(DiscountDetail.builder()
                        .discount(discount)
                        .order(order)
                        .discountedAmount(discountAmount.doubleValue())
                        .build());
            }
        }
    }

    // Phương thức hỗ trợ để cập nhật tồn kho
    private void updateInventory(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            if (item.getProductVariant() != null) {
                var variant = item.getProductVariant();
                long newQty = variant.getQuantity() - item.getQuantity();
                if (newQty < 0) {
                    throw new IllegalArgumentException("Sản phẩm " + variant.getName() + " không đủ tồn kho.");
                }
                variant.setQuantity(newQty);
            } else if (item.getProduct() != null) {
                var product = item.getProduct();
                long newQty = product.getMinimalQuantity() - item.getQuantity();
                if (newQty < 0) {
                    throw new IllegalArgumentException("Sản phẩm " + product.getName() + " không đủ tồn kho.");
                }
                product.setMinimalQuantity(newQty);
            }
        }
    }

    /** Kiểm tra xem phương thức vận chuyển có phù hợp với đơn hàng không dựa trên giá trị và trọng lượng*/
    private String validateShippingMethod(Order order, ShippingMethod shippingMethod) {
        // Tính toán tổng trọng lượng đơn hàng
        Double totalWeight = calculateOrderWeight(order);

        // Lấy tổng giá trị đơn hàng
        BigDecimal totalAmount = order.getTotalAmount();

        // Kiểm tra điều kiện tối thiểu về giá trị
        if (shippingMethod.getMinimumOrderPriceAmount() != null &&
                totalAmount.doubleValue() < shippingMethod.getMinimumOrderPriceAmount()) {
            return "Giá trị đơn hàng (" + totalAmount + ") thấp hơn giá trị tối thiểu yêu cầu ("
                    + shippingMethod.getMinimumOrderPriceAmount() + ") cho phương thức vận chuyển này.";
        }

        // Kiểm tra điều kiện tối đa về giá trị
        if (shippingMethod.getMaximumOrderPriceAmount() != null &&
                totalAmount.doubleValue() > shippingMethod.getMaximumOrderPriceAmount()) {
            return "Giá trị đơn hàng (" + totalAmount + ") cao hơn giá trị tối đa cho phép ("
                    + shippingMethod.getMaximumOrderPriceAmount() + ") cho phương thức vận chuyển này.";
        }

        // Kiểm tra điều kiện tối thiểu về trọng lượng
        if (shippingMethod.getMinimumOrderWeight() != null &&
                totalWeight < shippingMethod.getMinimumOrderWeight()) {
            return "Trọng lượng đơn hàng (" + totalWeight + ") thấp hơn trọng lượng tối thiểu yêu cầu ("
                    + shippingMethod.getMinimumOrderWeight() + ") cho phương thức vận chuyển này.";
        }

        // Kiểm tra điều kiện tối đa về trọng lượng
        if (shippingMethod.getMaximumOrderWeight() != null &&
                totalWeight > shippingMethod.getMaximumOrderWeight()) {
            return "Trọng lượng đơn hàng (" + totalWeight + ") cao hơn trọng lượng tối đa cho phép ("
                    + shippingMethod.getMaximumOrderWeight() + ") cho phương thức vận chuyển này.";
        }

        // Kiểm tra vùng vận chuyển
        if (order.getShippingAddress() != null && shippingMethod.getShippingZone() != null) {
            ShippingZone methodZone = shippingMethod.getShippingZone();
            ShippingZone orderZone = determineShippingZone(order.getShippingAddress(),
                    List.of(order.getSeller().getAddress()));

            if (!methodZone.getId().equals(orderZone.getId())) {
                return "Phương thức vận chuyển này không áp dụng cho khu vực giao hàng của bạn.";
            }
        }

        // Đơn hàng phù hợp với tất cả các điều kiện
        return null;
    }

    /** Tính toán tổng trọng lượng của đơn hàng từ các sản phẩm/biến thể sản phẩm*/
    private Double calculateOrderWeight(Order order) {
        Double totalWeight = 0.0;

        for (OrderItem item : order.getOrderItems()) {
            if (item.getProductVariant() != null) {
                // Sử dụng trọng lượng từ biến thể sản phẩm nếu có
                Double itemWeight = item.getProductVariant().getWeightVariant() != null ?
                        item.getProductVariant().getWeightVariant() : 0.0;
                totalWeight += itemWeight * item.getQuantity();
            } else if (item.getProduct() != null) {
                // Sử dụng trọng lượng từ sản phẩm nếu không có biến thể
                Double itemWeight = item.getProduct().getWeight() != null ?
                        item.getProduct().getWeight() : 0.0;
                totalWeight += itemWeight * item.getQuantity();
            }
        }

        return totalWeight;
    }

    /**Tạo payment ban đầu cho đơn hàng với trạng thái "Chưa thanh toán"*/

    private Invoice createInvoiceForOrder(Order order, InvoiceStatus status) {
        String transactionId = generateTransactionId();

        Invoice invoice = Invoice.builder()
                .order(order)
                .invoiceStatus(status)
                .totalAmount(order.getTotalAmount())
                .transactionId(transactionId)
                .build();

        return invoiceRepository.save(invoice);
    }

    private void updateInvoiceStatus(Order order, Long invoiceStatusId) {
        // Tìm hóa đơn của đơn hàng
        Invoice invoice = invoiceRepository.findByOrderOrderId(order.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn cho đơn hàng ID: " + order.getOrderId()));

        // Lấy trạng thái hóa đơn mới
        InvoiceStatus newStatus = invoiceStatusRepository.findById(invoiceStatusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái hóa đơn với ID: " + invoiceStatusId));

        // Cập nhật trạng thái hóa đơn
        invoice.setInvoiceStatus(newStatus);

        // Lưu hóa đơn đã cập nhật
        invoiceRepository.save(invoice);
    }

    /**Thống kê doanh thu của seller theo khoảng thời gian*/
    public String calculateSellerRevenue(Long sellerId, Date fromDate, Date toDate) {
        if (sellerId == null) {
            throw new IllegalArgumentException("Seller ID không được để trống");
        }

        BigDecimal revenue = orderRepository.calculateRevenueBySeller(sellerId, fromDate, toDate);
        return "Doanh thu của Seller Id " + sellerId + " là: " + revenue + " VNĐ";
    }

}