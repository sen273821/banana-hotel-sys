package com.banana.hotel.module.roomtype;

import com.banana.hotel.common.Result;
import com.banana.hotel.module.order.OrderService;
import com.banana.hotel.module.room.entity.RoomType;
import com.banana.hotel.module.room.mapper.RoomTypeMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/room-types")
public class RoomTypeController {

    private final RoomTypeMapper roomTypeMapper;
    private final OrderService orderService;

    public RoomTypeController(RoomTypeMapper roomTypeMapper, OrderService orderService) {
        this.roomTypeMapper = roomTypeMapper;
        this.orderService = orderService;
    }

    @GetMapping
    public Result<List<RoomType>> list() {
        return Result.success(roomTypeMapper.selectList(null));
    }

    @GetMapping("/{id}/availability")
    public Result<Map<String, Object>> availability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        int available = orderService.getAvailableRoomCount(id, checkIn, checkOut);
        Map<String, Object> data = new HashMap<>();
        data.put("roomTypeId", id);
        data.put("available", available);
        data.put("checkIn", checkIn);
        data.put("checkOut", checkOut);
        return Result.success(data);
    }
}
