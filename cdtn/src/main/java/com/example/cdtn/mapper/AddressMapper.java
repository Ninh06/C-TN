package com.example.cdtn.mapper;

import com.example.cdtn.dtos.AddressDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.example.cdtn.dtos.warehouse.WarehouseDTO;
import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.entity.warehouse.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    //Address entity sang dto
    public AddressDTO toAddressDTO(Address address) {
        if(address == null) {
            return null;
        }
        AddressDTO addressDTO = AddressDTO.builder()
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
        if(address.getSeller() != null) {
            SellerDTO sellerDTO = SellerDTO.builder()
                    .sellerId(address.getSeller().getSellerId())
                    .build();
            addressDTO.setSeller(sellerDTO);
        }
        if(address.getWarehouse() != null) {
            WarehouseDTO warehouseDTO = WarehouseDTO.builder()
                    .id(address.getWarehouse().getId())
                    .nameWarehouse(address.getWarehouse().getNameWarehouse())
                    .companyName(address.getWarehouse().getCompanyName())
                    .build();
            addressDTO.setWarehouse(warehouseDTO);
        }

        return addressDTO;
    }

    public Address toAddressEntity(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }

        Address address = Address.builder()
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

        if (addressDTO.getSeller() != null) {
            Seller seller = Seller.builder()
                    .sellerId(addressDTO.getSeller().getSellerId())
                    .build();
            address.setSeller(seller);
        }

        if (addressDTO.getWarehouse() != null) {
            Warehouse warehouse = Warehouse.builder()
                    .id(addressDTO.getWarehouse().getId())
                    .nameWarehouse(addressDTO.getWarehouse().getNameWarehouse())
                    .companyName(addressDTO.getWarehouse().getCompanyName())
                    .build();
            address.setWarehouse(warehouse);
        }

        return address;
    }

}
