package com.example.cdtn.dtos.ships;

import com.example.cdtn.dtos.orders.OrderDTO;
import com.example.cdtn.entity.orders.Order;
import com.example.cdtn.entity.ships.ShippingZone;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingMethodDTO {

    private Long id;

    @NotBlank(message = "Tên phương thức vận chuyển không được để trống")
    @Size(max = 255, message = "Tên phương thức vận chuyển không được vượt quá 255 ký tự")
    private String methodName;

    @NotNull(message = "Giá trị tối đa của đơn hàng là bắt buộc")
    @PositiveOrZero(message = "Giá trị tối đa của đơn hàng phải là số không âm")
    private Double maximumOrderPriceAmount;

    @NotNull(message = "Trọng lượng tối đa là bắt buộc")
    @PositiveOrZero(message = "Trọng lượng tối đa phải là số không âm")
    private Double maximumOrderWeight;

    @NotNull(message = "Giá trị tối thiểu của đơn hàng là bắt buộc")
    @PositiveOrZero(message = "Giá trị tối thiểu của đơn hàng phải là số không âm")
    private Double minimumOrderPriceAmount;

    @NotNull(message = "Trọng lượng tối thiểu là bắt buộc")
    @PositiveOrZero(message = "Trọng lượng tối thiểu phải là số không âm")
    private Double minimumOrderWeight;

    @NotNull(message = "Phí vận chuyển là bắt buộc")
    @PositiveOrZero(message = "Phí vận chuyển phải là số không âm")
    private Double priceAmount;

    private List<OrderDTO> orders;

    private com.example.cdtn.dtos.ships.ShippingZoneDTO shippingZoneId;

    @NotBlank(message = "Đơn vị tiền tệ không được để trống")
    @Size(max = 10, message = "Đơn vị tiền tệ không được vượt quá 10 ký tự")
    private String currency;

    @NotNull(message = "Trạng thái mặc định là bắt buộc")
    private Boolean isDefault;
}
