package com.banana.hotel.module.room.service;

import com.banana.hotel.common.enums.RoomStatus;
import com.banana.hotel.module.room.entity.*;
import com.banana.hotel.module.room.mapper.*;
import com.banana.hotel.module.room.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomBoardService {

    private final RoomMapper roomMapper;
    private final FloorMapper floorMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final RoomAssignmentMapper assignmentMapper;

    public RoomBoardVO getBoardData(Long floorId) {
        RoomBoardVO boardVO = new RoomBoardVO();

        boardVO.setFloors(getFloors());
        List<Room> rooms = getRooms(floorId);
        boardVO.setRooms(buildRoomVOs(rooms));
        boardVO.setStatusSummary(buildStatusSummary(rooms));
        boardVO.setOccupancyRate(calcOccupancyRate(rooms));

        return boardVO;
    }

    private List<FloorVO> getFloors() {
        List<Floor> floors = floorMapper.selectList(
                new LambdaQueryWrapper<Floor>()
                        .orderByAsc(Floor::getSortOrder)
        );
        return floors.stream().map(f -> {
            FloorVO vo = new FloorVO();
            vo.setId(f.getId());
            vo.setName(f.getName());
            vo.setFloorNo(f.getFloorNo());
            return vo;
        }).collect(Collectors.toList());
    }

    private List<Room> getRooms(Long floorId) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (floorId != null) {
            wrapper.eq(Room::getFloorId, floorId);
        }
        wrapper.orderByAsc(Room::getSortOrder);
        return roomMapper.selectList(wrapper);
    }

    private List<RoomVO> buildRoomVOs(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, RoomType> typeMap = getTypeMap();
        Map<Long, RoomAssignment> assignMap = getActiveAssignments(rooms);

        return rooms.stream().map(room -> {
            RoomVO vo = new RoomVO();
            vo.setId(room.getId());
            vo.setRoomNumber(room.getRoomNumber());
            vo.setFloorId(room.getFloorId());

            RoomType type = typeMap.get(room.getRoomTypeId());
            if (type != null) {
                vo.setRoomTypeName(type.getName());
                vo.setRoomTypeIcon(type.getIcon());
            }

            vo.setStatus(RoomStatus.fromCode(room.getStatus()).name());

            RoomAssignment assignment = assignMap.get(room.getId());
            if (assignment != null) {
                RoomAssignmentVO aVO = new RoomAssignmentVO();
                aVO.setId(assignment.getId());
                aVO.setGuestName(assignment.getGuestName());
                aVO.setGuestPhone(assignment.getGuestPhone());
                aVO.setCheckInTime(assignment.getCheckInTime());
                aVO.setCheckOutTime(assignment.getCheckOutTime());
                vo.setAssignment(aVO);
            }

            return vo;
        }).collect(Collectors.toList());
    }

    private Map<Long, RoomType> getTypeMap() {
        List<RoomType> types = roomTypeMapper.selectList(null);
        return types.stream().collect(
                Collectors.toMap(RoomType::getId, t -> t)
        );
    }

    private Map<Long, RoomAssignment> getActiveAssignments(List<Room> rooms) {
        List<Long> roomIds = rooms.stream()
                .map(Room::getId)
                .collect(Collectors.toList());

        List<RoomAssignment> assignments = assignmentMapper.selectList(
                new LambdaQueryWrapper<RoomAssignment>()
                        .in(RoomAssignment::getRoomId, roomIds)
                        .eq(RoomAssignment::getStatus, 0)
        );

        return assignments.stream().collect(
                Collectors.toMap(RoomAssignment::getRoomId, a -> a, (a, b) -> a)
        );
    }

    private Map<String, Integer> buildStatusSummary(List<Room> rooms) {
        Map<String, Integer> summary = new LinkedHashMap<>();
        for (RoomStatus status : RoomStatus.values()) {
            summary.put(status.name(), 0);
        }
        for (Room room : rooms) {
            String name = RoomStatus.fromCode(room.getStatus()).name();
            summary.merge(name, 1, Integer::sum);
        }
        return summary;
    }

    private Double calcOccupancyRate(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return 0.0;
        }
        long occupied = rooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.OCCUPIED.getCode()
                        || r.getStatus() == RoomStatus.BOOKED.getCode())
                .count();
        return Math.round(occupied * 1000.0 / rooms.size()) / 10.0;
    }
}
