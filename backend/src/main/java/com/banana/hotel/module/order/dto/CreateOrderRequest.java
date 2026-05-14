package com.banana.hotel.module.order.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CreateOrderRequest {

    @NotNull(message = "房型不能为空")
    private Long roomTypeId;

    @NotBlank(message = "入住人姓名不能为空")
    private String guestName;

    @NotBlank(message = "入住人手机号不能为空")
    private String guestPhone;

    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;

    @NotNull(message = "退房日期不能为空")
    private LocalDate checkOutDate;
}
