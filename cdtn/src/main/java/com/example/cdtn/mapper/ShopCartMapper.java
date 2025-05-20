package com.example.cdtn.mapper;

import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.example.cdtn.dtos.shopcart.CartItemDTO;
import com.example.cdtn.dtos.shopcart.ShoppingCartDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.shopcart.CartItem;
import com.example.cdtn.entity.shopcart.ShoppingCart;
import com.example.cdtn.entity.users.Buyer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShopCartMapper {
    public ShoppingCartDTO shoppingCartDTO(ShoppingCart shoppingCart) {
        if(shoppingCart == null) {
            return null;
        }

        ShoppingCartDTO shoppingCartDTO = ShoppingCartDTO.builder()
                .cartId(shoppingCart.getCartId())
                .totalPrice(shoppingCart.getTotalPrice())
                .build();

        if(shoppingCart.getBuyer() != null) {
            BuyerDTO buyerDTO = BuyerDTO.builder()
                    .buyerId(shoppingCart.getBuyer().getBuyerId())
                    .build();
            shoppingCartDTO.setBuyer(buyerDTO);
        }
        if(shoppingCart.getCartItems() != null && !shoppingCart.getCartItems().isEmpty()) {
            List<CartItemDTO> cartItemDTOs = shoppingCart.getCartItems().stream()
                    .map(cartItem -> CartItemDTO.builder()
                            .cartItemId(cartItem.getCartItemId())
                            .quantity(cartItem.getQuantity())
                            .totalPriceItem(cartItem.getTotalPriceItem())
                            .unitPrice(cartItem.getUnitPrice())
                            .build())
                    .collect(Collectors.toList());
            shoppingCartDTO.setCartItems(cartItemDTOs);
        } else {
            shoppingCartDTO.setCartItems(new ArrayList<>());
        }

        return shoppingCartDTO;
    }

    public ShoppingCart shoppingCartEntity(ShoppingCartDTO shoppingCartDTO) {
        if (shoppingCartDTO == null) {
            return null;
        }

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .cartId(shoppingCartDTO.getCartId())
                .totalPrice(shoppingCartDTO.getTotalPrice())
                .build();

        if (shoppingCartDTO.getBuyer() != null) {
            Buyer buyer = Buyer.builder()
                    .buyerId(shoppingCartDTO.getBuyer().getBuyerId())
                    .build();
            shoppingCart.setBuyer(buyer);
        }

        if (shoppingCartDTO.getCartItems() != null && !shoppingCartDTO.getCartItems().isEmpty()) {
            List<CartItem> cartItems = shoppingCartDTO.getCartItems().stream()
                    .map(cartItemDTO -> CartItem.builder()
                            .cartItemId(cartItemDTO.getCartItemId())
                            .quantity(cartItemDTO.getQuantity())
                            .totalPriceItem(cartItemDTO.getTotalPriceItem())
                            .unitPrice(cartItemDTO.getUnitPrice())
                            .build())
                    .collect(Collectors.toList());
            shoppingCart.setCartItems(cartItems);
        } else {
            shoppingCart.setCartItems(new ArrayList<>());
        }

        return shoppingCart;
    }

    public CartItemDTO toCartItemDTO(CartItem cartItem) {
        if(cartItem == null) {
            return  null;
        }

        CartItemDTO cartItemDTO = CartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .quantity(cartItem.getQuantity())
                .totalPriceItem(cartItem.getTotalPriceItem())
                .unitPrice(cartItem.getUnitPrice())
                .build();

        if(cartItem.getShoppingCart() != null) {
            ShoppingCartDTO shoppingCartDTO = ShoppingCartDTO.builder()
                    .cartId(cartItem.getShoppingCart().getCartId())
                    .totalPrice(cartItem.getShoppingCart().getTotalPrice())
                    .build();
            cartItemDTO.setShoppingCart(shoppingCartDTO);
        }
        if(cartItem.getProductVariant() != null) {
            ProductVariantDTO productVariantDTO = ProductVariantDTO.builder()
                    .id(cartItem.getProductVariant().getId())
                    .name(cartItem.getProductVariant().getName())
                    .price(cartItem.getProductVariant().getPrice())
                    .quantity(cartItem.getProductVariant().getQuantity())
                    .weightVariant(cartItem.getProductVariant().getWeightVariant())
                    .build();
            cartItemDTO.setProductVariant(productVariantDTO);
        }
        if(cartItem.getProduct() != null) {
            ProductDTO productDTO = ProductDTO.builder()
                    .productId(cartItem.getProduct().getProductId())
                    .name(cartItem.getProduct().getName())
                    .title(cartItem.getProduct().getTitle())
                    .description(cartItem.getProduct().getDescription())
                    .weight(cartItem.getProduct().getWeight())
                    .currency(cartItem.getProduct().getCurrency())
                    .minimalVariantPriceAmount(cartItem.getProduct().getMinimalVariantPriceAmount())
                    .minimalQuantity(cartItem.getProduct().getMinimalQuantity())
                    .availableForPurchase(cartItem.getProduct().getAvailableForPurchase())
                    .build();
            cartItemDTO.setProduct(productDTO);
        }
        return cartItemDTO;
    }

    public CartItem toCartItemEntity(CartItemDTO cartItemDTO) {
        if (cartItemDTO == null) {
            return null;
        }

        CartItem cartItem = CartItem.builder()
                .cartItemId(cartItemDTO.getCartItemId())
                .quantity(cartItemDTO.getQuantity())
                .totalPriceItem(cartItemDTO.getTotalPriceItem())
                .unitPrice(cartItemDTO.getUnitPrice())
                .build();

        if (cartItemDTO.getShoppingCart() != null) {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .cartId(cartItemDTO.getShoppingCart().getCartId())
                    .totalPrice(cartItemDTO.getShoppingCart().getTotalPrice())
                    .build();
            cartItem.setShoppingCart(shoppingCart);
        }
        if(cartItemDTO.getProduct() != null) {
            Product product = Product.builder()
                    .productId(cartItemDTO.getProduct().getProductId())
                    .name(cartItemDTO.getProduct().getName())
                    .title(cartItemDTO.getProduct().getTitle())
                    .description(cartItemDTO.getProduct().getDescription())
                    .weight(cartItemDTO.getProduct().getWeight())
                    .currency(cartItemDTO.getProduct().getCurrency())
                    .minimalVariantPriceAmount(cartItemDTO.getProduct().getMinimalVariantPriceAmount())
                    .minimalQuantity(cartItemDTO.getProduct().getMinimalQuantity())
                    .availableForPurchase(cartItemDTO.getProduct().getAvailableForPurchase())
                    .build();
            cartItem.setProduct(product);
        }
        if(cartItemDTO.getProductVariant() != null) {
            ProductVariant productVariant = ProductVariant.builder()
                    .id(cartItemDTO.getProductVariant().getId())
                    .name(cartItemDTO.getProductVariant().getName())
                    .price(cartItemDTO.getProductVariant().getPrice())
                    .quantity(cartItemDTO.getProductVariant().getQuantity())
                    .weightVariant(cartItemDTO.getProductVariant().getWeightVariant())
                    .build();
            cartItem.setProductVariant(productVariant);
        }

        return cartItem;
    }


}
