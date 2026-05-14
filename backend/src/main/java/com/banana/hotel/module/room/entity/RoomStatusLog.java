package com.banana.hotel.module.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("room_status_log")
public class RoomStatusLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private Integer previousStatus;
    private Integer newStatus;
    private Long operatorId;
    private String operatorName;
    private String reason;
    private LocalDateTime createdAt;
}
