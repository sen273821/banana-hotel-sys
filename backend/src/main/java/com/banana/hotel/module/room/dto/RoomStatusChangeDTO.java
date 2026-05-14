package com.banana.hotel.module.room.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoomStatusChangeDTO {

    @NotNull(message = "新状态不能为空")
    private String newStatus;

    private String reason;
}
