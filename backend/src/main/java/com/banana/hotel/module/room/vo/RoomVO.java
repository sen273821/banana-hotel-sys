package com.banana.hotel.module.room.vo;

import lombok.Data;

@Data
public class RoomVO {

    private Long id;
    private String roomNumber;
    private Long floorId;
    private String roomTypeName;
    private String roomTypeIcon;
    private String status;
    private RoomAssignmentVO assignment;
}
