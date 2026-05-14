package com.banana.hotel.module.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("room_type")
public class RoomType {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String icon;
    private Integer maxGuests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
