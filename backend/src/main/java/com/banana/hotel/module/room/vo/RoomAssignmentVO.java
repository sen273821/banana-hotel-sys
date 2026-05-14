package com.banana.hotel.module.room.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomAssignmentVO {

    private Long id;
    private String guestName;
    private String guestPhone;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
