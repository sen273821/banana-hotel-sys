package com.banana.hotel.module.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("room")
public class Room {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String roomNumber;
    private Long floorId;
    private Long roomTypeId;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
