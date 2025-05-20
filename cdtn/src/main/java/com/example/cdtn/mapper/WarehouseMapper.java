package com.example.cdtn.mapper;

import com.example.cdtn.dtos.AddressDTO;
import com.example.cdtn.dtos.users.SellerDTO;
import com.example.cdtn.dtos.warehouse.WarehouseDTO;
import com.example.cdtn.entity.Address;
import com.example.cdtn.entity.users.Seller;
import com.example.cdtn.entity.warehouse.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseDTO toWarehouseDTO(Warehouse warehouse) {
      if(warehouse == null) {
          return null;
      }

      WarehouseDTO warehouseDTO = WarehouseDTO.builder()
              .id(warehouse.getId())
              .nameWarehouse(warehouse.getNameWarehouse())
              .companyName(warehouse.getCompanyName())
              .build();
      if(warehouse.getAddress() != null) {
          AddressDTO addressDTO = AddressDTO.builder()
                  .id(warehouse.getAddress().getId())
                  .streetAddress1(warehouse.getAddress().getStreetAddress1())
                  .streetAddress2(warehouse.getAddress().getStreetAddress2())
                  .city(warehouse.getAddress().getCity())
                  .postalCode(warehouse.getAddress().getPostalCode())
                  .country(warehouse.getAddress().getCountry())
                  .countryArea(warehouse.getAddress().getCountryArea())
                  .phone(warehouse.getAddress().getPhone())
                  .cityArea(warehouse.getAddress().getCityArea())
                  .build();
          warehouseDTO.setAddress(addressDTO);
      }
      if(warehouse.getSeller() != null) {
          SellerDTO sellerDTO = SellerDTO.builder()
                  .sellerId(warehouse.getSeller().getSellerId())
                  .build();
          warehouseDTO.setSeller(sellerDTO);
      }

      return warehouseDTO;
    }

    public Warehouse toWarehouseEntity(WarehouseDTO warehouseDTO) {
        if (warehouseDTO == null) {
            return null;
        }

        Warehouse warehouse = Warehouse.builder()
                .id(warehouseDTO.getId())
                .nameWarehouse(warehouseDTO.getNameWarehouse())
                .companyName(warehouseDTO.getCompanyName())
                .build();

        if (warehouseDTO.getAddress() != null) {
            Address address = Address.builder()
                    .id(warehouseDTO.getAddress().getId())
                    .streetAddress1(warehouseDTO.getAddress().getStreetAddress1())
                    .streetAddress2(warehouseDTO.getAddress().getStreetAddress2())
                    .city(warehouseDTO.getAddress().getCity())
                    .postalCode(warehouseDTO.getAddress().getPostalCode())
                    .country(warehouseDTO.getAddress().getCountry())
                    .countryArea(warehouseDTO.getAddress().getCountryArea())
                    .phone(warehouseDTO.getAddress().getPhone())
                    .cityArea(warehouseDTO.getAddress().getCityArea())
                    .build();
            warehouse.setAddress(address);
        }

        if (warehouseDTO.getSeller() != null) {
            Seller seller = Seller.builder()
                    .sellerId(warehouseDTO.getSeller().getSellerId())
                    .build();
            warehouse.setSeller(seller);
        }

        return warehouse;
    }

}
