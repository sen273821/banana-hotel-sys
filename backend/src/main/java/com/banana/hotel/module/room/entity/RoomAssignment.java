package com.banana.hotel.module.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("room_assignment")
public class RoomAssignment {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private Long orderId;
    private String guestName;
    private String guestPhone;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
