package com.banana.hotel.module.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("floor")
public class Floor {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long buildingId;
    private String name;
    private Integer floorNo;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
