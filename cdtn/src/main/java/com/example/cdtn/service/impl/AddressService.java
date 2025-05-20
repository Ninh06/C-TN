package com.example.cdtn.service.impl;

import com.example.cdtn.dtos.AddressDTO;
import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.entity.warehouse.Warehouse;
import com.example.cdtn.exceptions.OurException;
import com.example.cdtn.mapper.AddressMapper;
import com.example.cdtn.repository.AddressRepository;
import com.example.cdtn.repository.SellerRepository;
import com.example.cdtn.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;

    /** Lấy tất cả địa chỉ*/
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(addressMapper::toAddressDTO)
                .collect(Collectors.toList());
    }

    /**Lấy địa chỉ theo ID*/
    public AddressDTO getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy địa chỉ với ID: " + id));
        return addressMapper.toAddressDTO(address);
    }

    /**Thêm địa chỉ mới*/
    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        validateAddressData(addressDTO);

        Address address = addressMapper.toAddressEntity(addressDTO);

        // Đảo ngược quan hệ để tránh lỗi khi lưu (vì seller và warehouse là bên mappedBy)
        address.setSeller(null);
        address.setWarehouse(null);

        Address savedAddress = addressRepository.save(address);

        // Xử lý liên kết với Seller nếu có
        if (addressDTO.getSeller() != null && addressDTO.getSeller().getSellerId() != null) {
            Seller seller = sellerRepository.findById(addressDTO.getSeller().getSellerId())
                    .orElseThrow(() -> new OurException("Không tìm thấy Seller với ID: " +
                            addressDTO.getSeller().getSellerId()));
            seller.setAddress(savedAddress);
            sellerRepository.save(seller);
            savedAddress.setSeller(seller);
        }

        // Xử lý liên kết với Warehouse nếu có
        if (addressDTO.getWarehouse() != null && addressDTO.getWarehouse().getId() != null) {
            Warehouse warehouse = warehouseRepository.findById(addressDTO.getWarehouse().getId())
                    .orElseThrow(() -> new OurException("Không tìm thấy Warehouse với ID: " +
                            addressDTO.getWarehouse().getId()));
            warehouse.setAddress(savedAddress);
            warehouseRepository.save(warehouse);
            savedAddress.setWarehouse(warehouse);
        }

        return addressMapper.toAddressDTO(savedAddress);
    }

    @Transactional
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy địa chỉ với ID: " + id));

        // Thay vì validate toàn bộ DTO, chỉ cập nhật các trường không null
        // và giữ nguyên giá trị cũ nếu trường tương ứng trong DTO là null

        if (addressDTO.getStreetAddress1() != null) {
            existingAddress.setStreetAddress1(addressDTO.getStreetAddress1());
        }

        if (addressDTO.getStreetAddress2() != null) {
            existingAddress.setStreetAddress2(addressDTO.getStreetAddress2());
        }

        if (addressDTO.getCity() != null) {
            existingAddress.setCity(addressDTO.getCity());
        }

        if (addressDTO.getPostalCode() != null) {
            existingAddress.setPostalCode(addressDTO.getPostalCode());
        }

        if (addressDTO.getCountry() != null) {
            existingAddress.setCountry(addressDTO.getCountry());
        }

        if (addressDTO.getCountryArea() != null) {
            existingAddress.setCountryArea(addressDTO.getCountryArea());
        }

        if (addressDTO.getPhone() != null) {
            existingAddress.setPhone(addressDTO.getPhone());
        }

        if (addressDTO.getCityArea() != null) {
            existingAddress.setCityArea(addressDTO.getCityArea());
        }

        // Kiểm tra nếu có bất kỳ trường nào là chuỗi rỗng thì báo lỗi
        validateNonEmptyFields(existingAddress);

        // Lưu địa chỉ đã cập nhật
        Address updatedAddress = addressRepository.save(existingAddress);

        // Cập nhật liên kết với Seller nếu có thay đổi
        if (addressDTO.getSeller() != null && addressDTO.getSeller().getSellerId() != null) {
            // Nếu địa chỉ đã liên kết với seller khác, hủy liên kết cũ
            if (existingAddress.getSeller() != null &&
                    !existingAddress.getSeller().getSellerId().equals(addressDTO.getSeller().getSellerId())) {
                existingAddress.getSeller().setAddress(null);
                sellerRepository.save(existingAddress.getSeller());
            }

            Seller seller = sellerRepository.findById(addressDTO.getSeller().getSellerId())
                    .orElseThrow(() -> new OurException("Không tìm thấy Seller với ID: " +
                            addressDTO.getSeller().getSellerId()));
            seller.setAddress(updatedAddress);
            sellerRepository.save(seller);
            updatedAddress.setSeller(seller);
        } else if (addressDTO.getSeller() != null && addressDTO.getSeller().getSellerId() == null) {
            // Nếu DTO chỉ định rõ ràng rằng cần xóa liên kết seller (sellerId = null)
            if (existingAddress.getSeller() != null) {
                existingAddress.getSeller().setAddress(null);
                sellerRepository.save(existingAddress.getSeller());
                updatedAddress.setSeller(null);
            }
        }
        // Nếu addressDTO.getSeller() là null thì giữ nguyên liên kết cũ

        // Cập nhật liên kết với Warehouse nếu có thay đổi
        if (addressDTO.getWarehouse() != null && addressDTO.getWarehouse().getId() != null) {
            // Nếu địa chỉ đã liên kết với warehouse khác, hủy liên kết cũ
            if (existingAddress.getWarehouse() != null &&
                    !existingAddress.getWarehouse().getId().equals(addressDTO.getWarehouse().getId())) {
                existingAddress.getWarehouse().setAddress(null);
                warehouseRepository.save(existingAddress.getWarehouse());
            }

            Warehouse warehouse = warehouseRepository.findById(addressDTO.getWarehouse().getId())
                    .orElseThrow(() -> new OurException("Không tìm thấy Warehouse với ID: " +
                            addressDTO.getWarehouse().getId()));
            warehouse.setAddress(updatedAddress);
            warehouseRepository.save(warehouse);
            updatedAddress.setWarehouse(warehouse);
        } else if (addressDTO.getWarehouse() != null && addressDTO.getWarehouse().getId() == null) {
            // Nếu DTO chỉ định rõ ràng rằng cần xóa liên kết warehouse (id = null)
            if (existingAddress.getWarehouse() != null) {
                existingAddress.getWarehouse().setAddress(null);
                warehouseRepository.save(existingAddress.getWarehouse());
                updatedAddress.setWarehouse(null);
            }
        }
        // Nếu addressDTO.getWarehouse() là null thì giữ nguyên liên kết cũ

        return addressMapper.toAddressDTO(updatedAddress);
    }

    /**Xóa địa chỉ theo ID*/
    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new OurException("Không tìm thấy địa chỉ với ID: " + id));

        // Hủy liên kết với Seller trước khi xóa (nếu có)
        if (address.getSeller() != null) {
            address.getSeller().setAddress(null);
            sellerRepository.save(address.getSeller());
        }

        // Hủy liên kết với Warehouse trước khi xóa (nếu có)
        if (address.getWarehouse() != null) {
            address.getWarehouse().setAddress(null);
            warehouseRepository.save(address.getWarehouse());
        }

        addressRepository.deleteById(id);
    }

    /**Lấy địa chỉ của một Seller*/
    public AddressDTO getAddressBySellerId(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new OurException("Không tìm thấy Seller với ID: " + sellerId));

        if (seller.getAddress() == null) {
            throw new OurException("Seller với ID: " + sellerId + " chưa có địa chỉ");
        }

        return addressMapper.toAddressDTO(seller.getAddress());
    }

    /**Lấy địa chỉ của một Warehouse*/
    public AddressDTO getAddressByWarehouseId(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new OurException("Không tìm thấy Warehouse với ID: " + warehouseId));

        if (warehouse.getAddress() == null) {
            throw new OurException("Warehouse với ID: " + warehouseId + " chưa có địa chỉ");
        }

        return addressMapper.toAddressDTO(warehouse.getAddress());
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu địa chỉ
     * @param addressDTO DTO địa chỉ cần kiểm tra
     */
    private void validateAddressData(AddressDTO addressDTO) {
        if (addressDTO == null) {
            throw new OurException("Dữ liệu địa chỉ không được để trống");
        }

        if (addressDTO.getStreetAddress1() == null || addressDTO.getStreetAddress1().trim().isEmpty()) {
            throw new OurException("Địa chỉ đường phố 1 không được để trống");
        }

        if (addressDTO.getCity() == null || addressDTO.getCity().trim().isEmpty()) {
            throw new OurException("Tên thành phố không được để trống");
        }

        if (addressDTO.getCountry() == null || addressDTO.getCountry().trim().isEmpty()) {
            throw new OurException("Tên quốc gia không được để trống");
        }

        if (addressDTO.getPhone() == null || addressDTO.getPhone().trim().isEmpty()) {
            throw new OurException("Số điện thoại không được để trống");
        }
    }

    /**
     * Kiểm tra các trường không được để trống trong địa chỉ
     * @param address Đối tượng địa chỉ cần kiểm tra
     */
    private void validateNonEmptyFields(Address address) {
        if (address.getStreetAddress1() != null && address.getStreetAddress1().trim().isEmpty()) {
            throw new OurException("Địa chỉ đường phố 1 không được để trống");
        }

        if (address.getCity() != null && address.getCity().trim().isEmpty()) {
            throw new OurException("Tên thành phố không được để trống");
        }

        if (address.getCountry() != null && address.getCountry().trim().isEmpty()) {
            throw new OurException("Tên quốc gia không được để trống");
        }

        if (address.getPhone() != null && address.getPhone().trim().isEmpty()) {
            throw new OurException("Số điện thoại không được để trống");
        }
    }

}
