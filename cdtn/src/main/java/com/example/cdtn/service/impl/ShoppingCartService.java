package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.shopcart.ShoppingCartDTO;
import com.example.cdtn.entity.flashsale.ProductFlashSale;
import com.example.cdtn.entity.flashsale.ProductVariantFlashSale;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.shopcart.CartItem;
import com.example.cdtn.entity.shopcart.ShoppingCart;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.ProductMapper;
import com.example.cdtn.mapper.ShopCartMapper;
import com.example.cdtn.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {
    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository shoppingCartRepository;
    @Autowired
    private ShopCartMapper shopCartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductVariantFlashSaleRepository productVariantFlashSaleRepository;
    @Autowired
    private ProductFlashSaleRepository productFlashSaleRepository;

    @Transactional
    public ShoppingCartDTO addProductToCart(Long buyerId, Long productId, Long productVariantId, Integer quantity) {
        // Tìm buyer và giỏ hàng
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new OurException("Không tìm thấy người mua"));
        ShoppingCart shoppingCart = buyer.getShoppingCart();

        // Tìm sản phẩm theo productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OurException("Không tìm thấy sản phẩm"));

        CartItem cartItem;
        BigDecimal unitPrice;

        // Xử lý nếu có biến thể sản phẩm
        if (product.getProductVariants() != null && !product.getProductVariants().isEmpty()) {
            if (productVariantId == null) {
                throw new OurException("Phải chọn biến thể sản phẩm");
            }

            // Tìm ProductVariant
            ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                    .orElseThrow(() -> new OurException("Không tìm thấy biến thể sản phẩm"));

            // Kiểm tra ProductVariant có thuộc về product
            if (!product.getProductVariants().contains(productVariant)) {
                throw new OurException("Biến thể sản phẩm không thuộc về sản phẩm này");
            }

            // Kiểm tra số lượng tồn kho
            Long availableStock = productVariant.getQuantity();
            if (availableStock == null || availableStock <= 0) {
                throw new OurException("Phiên bản sản phẩm đã hết hàng");
            }

            Optional<CartItem> existingItem = shoppingCart.getCartItems().stream()
                    .filter(item -> item.getProduct().getProductId().equals(productId) &&
                            item.getProductVariant() != null &&
                            item.getProductVariant().getId().equals(productVariantId))
                    .findFirst();

            int existingQuantityInCart = existingItem.map(CartItem::getQuantity).orElse(0);
            if (existingQuantityInCart + quantity > availableStock) {
                throw new OurException("Số lượng yêu cầu (" + (existingQuantityInCart + quantity) +
                        ") vượt quá lượng hàng sẵn có (" + availableStock + ")");
            }

            // Kiểm tra flash sale
            ProductVariantFlashSale activeVariantFlashSale = null;
            ProductFlashSale activeProductFlashSale = null;
            List<ProductVariantFlashSale> variantFlashSales = productVariantFlashSaleRepository
                    .findByProductVariantIdAndIsActiveTrue(productVariant.getId());
            activeVariantFlashSale = variantFlashSales.stream()
                    .filter(vfs -> vfs.getProductFlashSale().getFlashSale().getIsActive())
                    .findFirst()
                    .orElse(null);

            if (activeVariantFlashSale != null) {
                activeProductFlashSale = activeVariantFlashSale.getProductFlashSale();
                validateAndUpdateQuota(activeProductFlashSale, existingQuantityInCart + quantity);
                unitPrice = BigDecimal.valueOf(activeVariantFlashSale.getFlashSalePrice());
            } else {
                unitPrice = productVariant.getPrice();
            }

            // Cập nhật hoặc thêm mới cart item
            if (existingItem.isPresent()) {
                cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItem.setTotalPriceItem(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            } else {
                cartItem = CartItem.builder()
                        .shoppingCart(shoppingCart)
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .totalPriceItem(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                        .productVariant(productVariant)
                        .product(product)
                        .createdAt(new Date())
                        .build();
                shoppingCart.getCartItems().add(cartItem);
            }

        } else {
            // Trường hợp không có biến thể
            Long availableStock = product.getMinimalQuantity();
            if (availableStock == null || availableStock <= 0) {
                throw new OurException("Sản phẩm đã hết hàng");
            }

            Optional<CartItem> existingItem = shoppingCart.getCartItems().stream()
                    .filter(item -> item.getProduct().getProductId().equals(productId) && item.getProductVariant() == null)
                    .findFirst();

            int existingQuantityInCart = existingItem.map(CartItem::getQuantity).orElse(0);
            if (existingQuantityInCart + quantity > availableStock) {
                throw new OurException("Số lượng yêu cầu (" + (existingQuantityInCart + quantity) +
                        ") vượt quá số lượng có sẵn (" + availableStock + ")");
            }

            // Kiểm tra flash sale
            ProductFlashSale activeProductFlashSale = null;
            List<ProductFlashSale> productFlashSales = product.getProductFlashSales() != null
                    ? product.getProductFlashSales().stream()
                    .filter(pfs -> pfs.getIsActive() && pfs.getFlashSale().getIsActive())
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            activeProductFlashSale = productFlashSales.stream()
                    .findFirst()
                    .orElse(null);

            if (activeProductFlashSale != null) {
                validateAndUpdateQuota(activeProductFlashSale, existingQuantityInCart + quantity);
                unitPrice = BigDecimal.valueOf(activeProductFlashSale.getFlashSalePrice());
            } else {
                unitPrice = BigDecimal.valueOf(product.getMinimalVariantPriceAmount());
            }

            // Cập nhật hoặc thêm mới cart item
            if (existingItem.isPresent()) {
                cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItem.setTotalPriceItem(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            } else {
                cartItem = CartItem.builder()
                        .shoppingCart(shoppingCart)
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .totalPriceItem(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                        .productVariant(null)
                        .product(product)
                        .createdAt(new Date())
                        .build();
                shoppingCart.getCartItems().add(cartItem);
            }
        }

        // Lưu CartItem vào giỏ hàng
        cartItem = cartItemRepository.save(cartItem);

        // Cập nhật tổng giá trị giỏ hàng
        updateShoppingCartTotalPrice(shoppingCart);

        // Chuyển đổi về DTO và trả về
        return shopCartMapper.shoppingCartDTO(shoppingCart);
    }

    @Transactional
    public void updateCartItemPrices(ShoppingCart shoppingCart) {
        List<CartItem> cartItems = shoppingCart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            return;
        }

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            ProductVariant productVariant = cartItem.getProductVariant();
            Integer quantity = cartItem.getQuantity();
            BigDecimal newUnitPrice;

            if (productVariant != null) {
                // Kiểm tra flash sale cho biến thể
                List<ProductVariantFlashSale> variantFlashSales = productVariantFlashSaleRepository
                        .findByProductVariantIdAndIsActiveTrue(productVariant.getId());
                ProductVariantFlashSale activeVariantFlashSale = variantFlashSales.stream()
                        .filter(vfs -> vfs.getProductFlashSale().getFlashSale().getIsActive())
                        .findFirst()
                        .orElse(null);

                if (activeVariantFlashSale != null) {
                    newUnitPrice = BigDecimal.valueOf(activeVariantFlashSale.getFlashSalePrice());
                } else {
                    newUnitPrice = productVariant.getPrice();
                }
            } else {
                // Kiểm tra flash sale cho sản phẩm
                List<ProductFlashSale> productFlashSales = product.getProductFlashSales() != null
                        ? product.getProductFlashSales().stream()
                        .filter(pfs -> pfs.getIsActive() && pfs.getFlashSale().getIsActive())
                        .collect(Collectors.toList())
                        : new ArrayList<>();

                ProductFlashSale activeProductFlashSale = productFlashSales.stream()
                        .findFirst()
                        .orElse(null);

                if (activeProductFlashSale != null) {
                    newUnitPrice = BigDecimal.valueOf(activeProductFlashSale.getFlashSalePrice());
                } else {
                    newUnitPrice = BigDecimal.valueOf(product.getMinimalVariantPriceAmount());
                }
            }

            // Cập nhật giá nếu khác
            if (!newUnitPrice.equals(cartItem.getUnitPrice())) {
                cartItem.setUnitPrice(newUnitPrice);
                cartItem.setTotalPriceItem(newUnitPrice.multiply(BigDecimal.valueOf(quantity)));
                cartItemRepository.save(cartItem);
            }
        }

        // Cập nhật tổng giá trị giỏ hàng
        updateShoppingCartTotalPrice(shoppingCart);
    }

    private void validateAndUpdateQuota(ProductFlashSale productFlashSale, Integer requestedQuantity) {
        Integer quota = productFlashSale.getQuota();
        Integer soldCount = productFlashSale.getSoldCount() != null ? productFlashSale.getSoldCount() : 0;

        if (quota == null || quota <= 0) {
            throw new OurException("Số lượng quota của flash sale không hợp lệ cho sản phẩm ID: " +
                    productFlashSale.getProduct().getProductId());
        }

        int availableQuota = quota - soldCount;
        if (requestedQuantity > availableQuota) {
            throw new OurException("Số lượng yêu cầu (" + requestedQuantity +
                    ") vượt quá quota còn lại (" + availableQuota + ") cho sản phẩm flash sale ID: " +
                    productFlashSale.getId());
        }

        // Cập nhật soldCount
        productFlashSale.setSoldCount(soldCount + requestedQuantity);

        // Nếu quota bằng soldCount, đặt isActive = false
        if (productFlashSale.getSoldCount() >= quota) {
            productFlashSale.setIsActive(false);
            List<ProductVariantFlashSale> variantFlashSales = productVariantFlashSaleRepository
                    .findByProductFlashSaleId(productFlashSale.getId());
            variantFlashSales.forEach(vfs -> vfs.setIsActive(false));
            productVariantFlashSaleRepository.saveAll(variantFlashSales);
        }

        // Lưu ProductFlashSale
        productFlashSaleRepository.save(productFlashSale);
    }


    public ShoppingCartDTO removeProductFromCart(Long buyerId, Long cartItemId) {
        // Tìm buyer và giỏ hàng
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new OurException("Không tìm thấy người mua"));
        ShoppingCart shoppingCart = buyer.getShoppingCart();

        // Tìm CartItem theo cartItemId
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new OurException("Không tìm thấy mặt hàng trong giỏ hàng"));

        // Kiểm tra xem CartItem có thuộc giỏ hàng của Buyer không
        if (!cartItem.getShoppingCart().getCartId().equals(shoppingCart.getCartId())) {
            throw new OurException("Mặt hàng trong giỏ hàng không thuộc về giỏ hàng này");
        }

        // Xóa CartItem khỏi giỏ hàng
        shoppingCart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        // Cập nhật tổng giá trị giỏ hàng sau khi xóa sản phẩm
        updateShoppingCartTotalPrice(shoppingCart);

        // Chuyển đổi về DTO và trả về
        return shopCartMapper.shoppingCartDTO(shoppingCart);
    }

    public ShoppingCartDTO updateCartItemQuantity(Long buyerId, Long cartItemId, Integer newQuantity) {
        // Tìm buyer và giỏ hàng
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new OurException("Không tìm thấy người mua"));
        ShoppingCart shoppingCart = buyer.getShoppingCart();

        // Tìm CartItem theo cartItemId
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new OurException("Không tìm thấy mặt hàng trong giỏ hàng"));

        // Kiểm tra xem CartItem có thuộc giỏ hàng của Buyer không
        if (!cartItem.getShoppingCart().getCartId().equals(shoppingCart.getCartId())) {
            throw new OurException("Mặt hàng trong giỏ hàng không thuộc về giỏ hàng này");
        }

        if (newQuantity <= 0) {
            // Nếu số lượng <= 0, xóa sản phẩm khỏi giỏ hàng
            shoppingCart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            // Cập nhật số lượng và tổng giá
            cartItem.setQuantity(newQuantity);
            cartItem.setTotalPriceItem(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(cartItem);
        }

        // Cập nhật tổng giá trị giỏ hàng
        updateShoppingCartTotalPrice(shoppingCart);

        // Chuyển đổi về DTO và trả về
        return shopCartMapper.shoppingCartDTO(shoppingCart);
    }

    /**Lấy thông tin giỏ hàng hiện tại của người mua*/
    public ShoppingCartDTO getShoppingCartByBuyerId(Long buyerId) {
        // Tìm buyer theo ID
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new OurException("Không tìm thấy người mua"));

        // Lấy giỏ hàng của buyer
        ShoppingCart shoppingCart = buyer.getShoppingCart();

        // Nếu người dùng chưa có giỏ hàng, tạo mới
        if (shoppingCart == null) {
            shoppingCart = ShoppingCart.builder()
                    .buyer(buyer)
                    .totalPrice(BigDecimal.ZERO)
                    .cartItems(new ArrayList<>())
                    .build();
            shoppingCart = shoppingCartRepository.save(shoppingCart);
            buyer.setShoppingCart(shoppingCart);
            buyerRepository.save(buyer);
        }

        // Chuyển đổi sang DTO và trả về
        return shopCartMapper.shoppingCartDTO(shoppingCart);
    }

    /** Xóa toàn bộ giỏ hàng của người mua */
    public ShoppingCartDTO clearShoppingCart(Long buyerId) {
        // Tìm buyer theo ID
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new OurException("Không tìm thấy người mua"));

        // Lấy giỏ hàng của buyer
        ShoppingCart shoppingCart = buyer.getShoppingCart();

        if (shoppingCart != null && shoppingCart.getCartItems() != null && !shoppingCart.getCartItems().isEmpty()) {
            // Xóa tất cả các mục trong giỏ hàng
            cartItemRepository.deleteAll(shoppingCart.getCartItems());

            // Xóa danh sách các mục trong đối tượng giỏ hàng
            shoppingCart.getCartItems().clear();

            // Cập nhật tổng giá trị về 0
            shoppingCart.setTotalPrice(BigDecimal.ZERO);
            shoppingCartRepository.save(shoppingCart);
        } else if (shoppingCart == null) {
            // Nếu người dùng chưa có giỏ hàng, tạo mới
            shoppingCart = ShoppingCart.builder()
                    .buyer(buyer)
                    .totalPrice(BigDecimal.ZERO)
                    .cartItems(new ArrayList<>())
                    .build();
            shoppingCart = shoppingCartRepository.save(shoppingCart);
            buyer.setShoppingCart(shoppingCart);
            buyerRepository.save(buyer);
        }

        // Cập nhật lại tổng giá trị giỏ hàng (đảm bảo giá trị là 0)
        updateShoppingCartTotalPrice(shoppingCart);

        // Chuyển đổi sang DTO và trả về
        return shopCartMapper.shoppingCartDTO(shoppingCart);
    }

    /**Lấy đối tượng entity ShoppingCart của người mua*/
    public ShoppingCart getShoppingCartEntityByBuyerId(Long buyerId) {
        // Tìm buyer theo ID
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new OurException("Không tìm thấy người mua"));

        // Lấy giỏ hàng của buyer
        ShoppingCart shoppingCart = buyer.getShoppingCart();

        // Nếu người dùng chưa có giỏ hàng, tạo mới
        if (shoppingCart == null) {
            shoppingCart = ShoppingCart.builder()
                    .buyer(buyer)
                    .totalPrice(BigDecimal.ZERO)
                    .cartItems(new ArrayList<>())
                    .build();
            shoppingCart = shoppingCartRepository.save(shoppingCart);
            buyer.setShoppingCart(shoppingCart);
            buyerRepository.save(buyer);
        }

        return shoppingCart;
    }

    private void updateShoppingCartTotalPrice(ShoppingCart shoppingCart) {
        // Tính tổng giá trị giỏ hàng
        BigDecimal totalPrice = shoppingCart.getCartItems().stream()
                .map(CartItem::getTotalPriceItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        shoppingCart.setTotalPrice(totalPrice);

        // Lưu lại giỏ hàng với giá trị đã cập nhật
        shoppingCartRepository.save(shoppingCart);
    }
}