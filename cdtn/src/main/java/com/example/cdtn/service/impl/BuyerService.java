package com.example.cdtn.service.impl;

import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.entity.shopcart.ShoppingCart;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.entity.users.User;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.repository.BuyerRepository;
import com.example.cdtn.repository.CartRepository;
import com.example.cdtn.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerService {
    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    public Buyer getBuyerById(Long buyerId) {
        return buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Buyer với id: " + buyerId));
    }

    @Transactional
    public void deleteBuyerById(Long buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new OurException("Không tìm thấy buyer với ID: " + buyerId));

        // Xóa các quan hệ một cách rõ ràng
        if (buyer.getShoppingCart() != null) {
            ShoppingCart cart = buyer.getShoppingCart();
            // Xóa các cart items
            if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
                cart.getCartItems().clear();
            }
            // Ngắt liên kết với buyer
            cart.setBuyer(null);
            cartRepository.save(cart);
            cartRepository.delete(cart);
            buyer.setShoppingCart(null);
        }

        if (buyer.getWishlist() != null) {
            Wishlist wishlist = buyer.getWishlist();
            // Xóa các sản phẩm trong wishlist
            if (wishlist.getProducts() != null && !wishlist.getProducts().isEmpty()) {
                wishlist.getProducts().clear();
            }
            // Ngắt liên kết với buyer
            wishlist.setBuyer(null);
            wishlistRepository.save(wishlist);
            wishlistRepository.delete(wishlist);
            buyer.setWishlist(null);
        }

        if (buyer.getShippingAddresses() != null && !buyer.getShippingAddresses().isEmpty()) {
            buyer.getShippingAddresses().clear();
        }

        if (buyer.getOrders() != null && !buyer.getOrders().isEmpty()) {
            buyer.getOrders().clear();
        }

        if (buyer.getReviews() != null && !buyer.getReviews().isEmpty()) {
            buyer.getReviews().clear();
        }

        if (buyer.getReturnOrders() != null && !buyer.getReturnOrders().isEmpty()) {
            buyer.getReturnOrders().clear();
        }

        // Ngắt liên kết với user
        User user = buyer.getUser();
        if (user != null) {
            user.setBuyer(null);
            buyer.setUser(null);
        }

        // Lưu buyer với các quan hệ đã được dọn dẹp
        buyerRepository.save(buyer);

        // Xóa buyer
        buyerRepository.delete(buyer);
    }

}
