package com.banana.hotel.module.room.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomStatusLogVO {

    private Long id;
    private String previousStatus;
    private String newStatus;
    private String operatorName;
    private String reason;
    private LocalDateTime createdAt;
}
