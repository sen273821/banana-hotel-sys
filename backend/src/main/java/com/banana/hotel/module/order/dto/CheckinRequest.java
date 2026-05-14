package com.banana.hotel.module.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CheckinRequest {

    @NotNull(message = "房间ID不能为空")
    private Long roomId;
}
