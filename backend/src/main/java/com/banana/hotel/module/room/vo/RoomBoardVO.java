package com.banana.hotel.module.room.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RoomBoardVO {

    private List<FloorVO> floors;
    private Map<String, Integer> statusSummary;
    private Double occupancyRate;
    private List<RoomVO> rooms;
}
