package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.auth.RegistrationRequestDTO;
import com.example.cdtn.dtos.auth.LoginRequestDTO;
import com.example.cdtn.dtos.auth.LoginResponseDTO;
import com.example.cdtn.dtos.users.UpdateUserRequestDTO;
import com.example.cdtn.dtos.users.UserDTO;
import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.Wishlist;
import com.example.cdtn.entity.shopcart.ShoppingCart;
import com.example.cdtn.entity.users.Buyer;
import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.entity.users.User;
import com.example.cdtn.entity.warehouse.Warehouse;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.UserMapper;
import com.example.cdtn.repository.*;
import com.example.cdtn.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BuyerService buyerService;

    /**Tạo đối tượng Buyer cho User với thông tin giỏ hàng và danh sách yêu thích*/
    private void createBuyerForUser(User user, RegistrationRequestDTO request) {
        // Tạo shopping cart mới
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .totalPrice(BigDecimal.ZERO)
                .createdAt(new Date())
                .cartItems(new ArrayList<>())
                .build();

        // Tạo wishlist mới
        Wishlist wishlist = Wishlist.builder()
                .products(new ArrayList<>())
                .createdAt(new Date())
                .build();

        // Tạo buyer
        Buyer buyer = Buyer.builder()
                .user(user)
                .createdAt(new Date())
                .shoppingCart(shoppingCart)
                .wishlist(wishlist)
                .shippingAddresses(new ArrayList<>())
                .orders(new ArrayList<>())
                .reviews(new ArrayList<>())
                .returnOrders(new ArrayList<>())
                .build();

        // Thiết lập mối quan hệ hai chiều
        shoppingCart.setBuyer(buyer);
        wishlist.setBuyer(buyer);

        // Lưu buyer (cascade sẽ tự động lưu shoppingCart và wishlist)
        buyerRepository.save(buyer);
        buyerRepository.flush(); // Đảm bảo dữ liệu được ghi vào cơ sở dữ liệu

        // Cập nhật user
        user.setBuyer(buyer);
    }



    /**Tạo đối tượng Seller cho User với thông tin địa chỉ*/
    private void createSellerForUser(User user, RegistrationRequestDTO request) {
        Seller seller = new Seller();
        seller.setUser(user);
        seller.setCreatedAt(new Date());

        // Khởi tạo danh sách sản phẩm rỗng
        seller.setProducts(new ArrayList<>());

        // Tạo địa chỉ cho người bán nếu có thông tin địa chỉ
        if (request.getStreetAddress1() != null || request.getCity() != null || request.getCountry() != null) {
            Address address = Address.builder()
                    .streetAddress1(request.getStreetAddress1())
                    .streetAddress2(request.getStreetAddress2())
                    .city(request.getCity())
                    .postalCode(request.getPostalCode())
                    .country(request.getCountry())
                    .countryArea(request.getCountryArea())
                    .phone(request.getPhone())
                    .cityArea(request.getCityArea())
                    .build();

            address = addressRepository.save(address);

            seller.setAddress(address);
            address.setSeller(seller);
        }

        // Tạo kho hàng (warehouse) cho người bán
        Warehouse warehouse = new Warehouse();
        warehouse.setNameWarehouse(request.getNameWarehouse());
        warehouse.setCompanyName(request.getCompanyName());
        warehouse.setSeller(seller);
        warehouse.setAddress(seller.getAddress());
        // Có thể thiết lập các thuộc tính khác của warehouse nếu cần
        seller.setWarehouse(warehouse);

        user.setSeller(seller);
    }

    @Transactional
    public UserDTO registerUser(RegistrationRequestDTO request) {

        User user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .userType(request.getUserType())
                .createdAt(new Date())
                .build();

        // Lưu user trước để có id
        User savedUser = userRepository.save(user);

        if ("BUYER".equals(request.getUserType())) {
            createBuyerForUser(savedUser, request);

        } else if ("SELLER".equals(request.getUserType())) {
            createSellerForUser(savedUser, request);

        }

        savedUser = userRepository.save(savedUser);

        return userMapper.toUserDTO(savedUser);
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // Sử dụng AuthenticationManager để xác thực thông tin đăng nhập
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );

            // Nếu xác thực thành công, thiết lập SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy thông tin user details từ authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Tìm user từ repository để lấy thông tin đầy đủ
            User user = userRepository.findByUserName(userDetails.getUsername())
                    .orElseThrow(() -> new OurException("Không tìm thấy người dùng"));

            // Tạo JWT token cho user
            String jwtToken = jwtUtils.generateToken(userDetails);

            // Cập nhật thời gian đăng nhập cuối cùng
            userRepository.save(user);

            // Tạo response chứa thông tin người dùng và token
            LoginResponseDTO responseDTO = new LoginResponseDTO();
            responseDTO.setMessage("Đăng nhập thành công!");
            responseDTO.setToken(jwtToken);
            responseDTO.setUserType(user.getUserType());

            return responseDTO;

        } catch (BadCredentialsException e) {
            throw new OurException("Tên đăng nhập hoặc mật khẩu không chính xác");
        } catch (Exception e) {
            throw new OurException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    /**Hiển thị User theo id*/
    @Transactional
    public UserDTO getUserById(Long userId) {
        // Tìm kiếm người dùng theo userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OurException("Không tìm thấy người dùng có ID: " + userId));

        // Chuyển đối tượng User thành UserDTO
        return userMapper.toUserDTO(user);
    }

    /**Hiển thị all User*/
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserDTO)
                .toList();
    }
    public Page<UserDTO> searchUsers(String userName, String email, String fullName, String userType, Pageable pageable) {
        Page<User> usersPage = userRepository.searchUsers(userName, email, fullName, userType, pageable);
        return usersPage.map(userMapper::toUserDTO);
    }


    /**Update User*/
    @Transactional
    public UserDTO updateUser(Long userId, UpdateUserRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OurException("Không tìm thấy người dùng"));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        }

        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    /**Delete User*/
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OurException("Không tìm thấy người dùng với id: " + userId));

        userRepository.delete(user);
    }

    @Transactional
    public UserDTO convertBuyerToSeller(Long userId, RegistrationRequestDTO request) {
        try {
            // Lấy User theo userId
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("Không tìm thấy user với ID: " + userId));

            // Kiểm tra xem user hiện tại có phải là buyer không
            Buyer buyer = buyerRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new OurException("User không phải là buyer hoặc chưa đăng ký buyer."));

            // Quan trọng: Tắt mối quan hệ giữa User và Buyer trước khi xóa Buyer
            user.setBuyer(null);
            userRepository.save(user);

            // Đổi userType thành SELLER
            user.setUserType("SELLER");
            user = userRepository.save(user);

            // Xóa buyer khỏi DB một cách riêng biệt
            // Quan trọng: Không xóa Buyer trong cùng giao dịch, tạo một giao dịch mới
            deleteBuyerSafely(buyer.getBuyerId());

            // Tạo Seller mới
            createSellerForUser(user, request);

            // Lưu lại user
            user = userRepository.save(user);

            return userMapper.toUserDTO(user);
        } catch (Exception e) {
            throw new OurException("Lỗi khi chuyển đổi Buyer sang Seller: " + e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void deleteBuyerSafely(Long buyerId) {
        try {
            buyerService.deleteBuyerById(buyerId);
        } catch (Exception e) {
            throw new OurException("Không thể xóa buyer: " + e.getMessage());
        }
    }



}