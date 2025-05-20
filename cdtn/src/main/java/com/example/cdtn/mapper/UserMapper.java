package com.example.cdtn.mapper;

import com.example.cdtn.dtos.AddressDTO;
import com.example.cdtn.dtos.ReviewDTO;
import com.example.cdtn.dtos.WishlistDTO;
import com.example.cdtn.dtos.orders.OrderItemDTO;
import com.example.cdtn.dtos.orders.ReturnOrderDTO;
import com.example.cdtn.dtos.products.CategoryDTO;
import com.example.cdtn.dtos.products.ProductDTO;
import com.example.cdtn.dtos.products.ProductTypeDTO;
import com.example.cdtn.dtos.products.ProductVariantDTO;
import com.example.cdtn.dtos.users.BuyerDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.example.cdtn.dtos.users.UserDTO;
import com.example.cdtn.dtos.warehouse.WarehouseDTO;
import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.Review;
import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.entity.orders.ReturnOrder;
import com.example.cdtn.entity.products.Category;
import com.example.cdtn.entity.products.Product;
import com.example.cdtn.entity.products.ProductType;
import com.example.cdtn.entity.products.ProductVariant;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.entity.users.User;
import com.example.cdtn.entity.warehouse.Warehouse;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    /**
     * Chuyển đổi User entity thành UserDTO
     */
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = UserDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt())
                .userType(user.getUserType())
                .build();

        // Không chuyển mật khẩu sang DTO vì lý do bảo mật

        // Chuyển đổi Buyer nếu có
        if (user.getBuyer() != null) {
            userDTO.setBuyer(toBuyerDTO(user.getBuyer()));
        }

        // Chuyển đổi Seller nếu có
        if (user.getSeller() != null) {
            userDTO.setSeller(toSellerDTO(user.getSeller()));
        }

        return userDTO;
    }

    /**
     * Chuyển đổi UserDTO thành User entity
     */
    public User toUserEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = User.builder()
                .userId(userDTO.getUserId())
                .userName(userDTO.getUserName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword()) // Chú ý: password sẽ cần được mã hóa trước khi lưu
                .fullName(userDTO.getFullName())
                .createdAt(userDTO.getCreatedAt())
                .userType(userDTO.getUserType())
                .build();
        if (userDTO.getBuyer() != null) {
            user.setBuyer(toBuyerEntity(userDTO.getBuyer()));
        }

        // Chuyển đổi Seller nếu có
        if (userDTO.getSeller() != null) {
            user.setSeller(toSellerEntity(userDTO.getSeller()));
        }

        return user;
    }

    /**
     * Chuyển đổi Buyer entity thành BuyerDTO
     */
    public BuyerDTO toBuyerDTO(Buyer buyer) {
        if (buyer == null) {
            return null;
        }

        return BuyerDTO.builder()
                .buyerId(buyer.getBuyerId())
                .createdAt(buyer.getCreatedAt())
                .shippingAddresses(new ArrayList<>()) // Cần triển khai mapper riêng cho shippingAddresses
                .orders(new ArrayList<>()) // Cần triển khai mapper riêng cho orders
                .reviews(new ArrayList<>()) // Cần triển khai mapper riêng cho reviews
                .returnOrders(new ArrayList<>()) // Cần triển khai mapper riêng cho returnOrders
                .build();
    }

    public Buyer toBuyerEntity(BuyerDTO buyerDTO) {
        if (buyerDTO == null) {
            return null;
        }

        return Buyer.builder()
                .buyerId(buyerDTO.getBuyerId())
                .createdAt(buyerDTO.getCreatedAt())
                // Không set user hoặc các danh sách liên quan ở đây để tránh vòng lặp hoặc lỗi không cần thiết
                .shippingAddresses(new ArrayList<>()) // Cần mapper riêng nếu muốn map chi tiết
                .orders(new ArrayList<>())
                .reviews(new ArrayList<>())
                .returnOrders(new ArrayList<>())
                .build();
    }


    /**
     * Chuyển đổi Seller entity thành SellerDTO
     */
    public SellerDTO toSellerDTO(Seller seller) {
        if (seller == null) {
            return null;
        }

        SellerDTO sellerDTO = SellerDTO.builder()
                .sellerId(seller.getSellerId())
                .createdAt(seller.getCreatedAt())
                .products(new ArrayList<>()) // Cần triển khai mapper riêng cho products
                .build();

        // Chuyển đổi Address nếu có
        if (seller.getAddress() != null) {
            sellerDTO.setAddress(toAddressDTO(seller.getAddress()));
        }

        if(seller.getWarehouse() != null) {
            sellerDTO.setWarehouse(toWarehouseDTO(seller.getWarehouse()));
        }
        return sellerDTO;
    }

    private Seller toSellerEntity(SellerDTO sellerDTO) {
        if (sellerDTO == null) {
            return null;
        }

        Seller seller = Seller.builder()
                .sellerId(sellerDTO.getSellerId())
                .createdAt(sellerDTO.getCreatedAt())
                .products(new ArrayList<>()) // Không ánh xạ chi tiết để tránh vòng lặp
                .build();

        // Chuyển đổi Address nếu có
        if (sellerDTO.getAddress() != null) {
            seller.setAddress(toAddressEntity(sellerDTO.getAddress()));
        }

        // Chuyển đổi Warehouse nếu có
        if (sellerDTO.getWarehouse() != null) {
            seller.setWarehouse(toWarehouseEntity(sellerDTO.getWarehouse()));
        }

        return seller;
    }


    /**
     * Chuyển đổi Address entity thành AddressDTO
     */
    private AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }

        return AddressDTO.builder()
                .id(address.getId())
                .streetAddress1(address.getStreetAddress1())
                .streetAddress2(address.getStreetAddress2())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .countryArea(address.getCountryArea())
                .phone(address.getPhone())
                .cityArea(address.getCityArea())
                .build();
        // Không map seller và warehouse để tránh vòng lặp vô hạn
    }

    /**
     * Chuyển đổi AddressDTO thành Address entity
     */
    public Address toAddressEntity(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }

        return Address.builder()
                .id(addressDTO.getId())
                .streetAddress1(addressDTO.getStreetAddress1())
                .streetAddress2(addressDTO.getStreetAddress2())
                .city(addressDTO.getCity())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry())
                .countryArea(addressDTO.getCountryArea())
                .phone(addressDTO.getPhone())
                .cityArea(addressDTO.getCityArea())
                .build();
        // Không map seller và warehouse để tránh vòng lặp vô hạn
    }

    /**
     * Chuyển đổi Warehouse entity thành WarehouseDTO
     */
    private WarehouseDTO toWarehouseDTO(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }

        return WarehouseDTO.builder()
                .id(warehouse.getId()) // ID kho hàng
                .nameWarehouse(warehouse.getNameWarehouse()) // Tên kho hàng
                .companyName(warehouse.getCompanyName()) // Tên công ty
                .build();
    }

    /**
     * Chuyển đổi WarehouseDTO thành Warehouse entity
     */
    private Warehouse toWarehouseEntity(WarehouseDTO warehouseDTO) {
        if (warehouseDTO == null) {
            return null;
        }

        return Warehouse.builder()
                .id(warehouseDTO.getId()) // ID kho hàng
                .nameWarehouse(warehouseDTO.getNameWarehouse()) // Tên kho hàng
                .companyName(warehouseDTO.getCompanyName()) // Tên công ty
                .build();
    }

}
