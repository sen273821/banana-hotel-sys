package com.banana.hotel.module.room.service;

import com.banana.hotel.common.enums.RoomStatus;
import com.banana.hotel.module.room.entity.*;
import com.banana.hotel.module.room.mapper.*;
import com.banana.hotel.module.room.vo.RoomBoardVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomBoardServiceTest {

    @Mock
    private RoomMapper roomMapper;
    @Mock
    private FloorMapper floorMapper;
    @Mock
    private RoomTypeMapper roomTypeMapper;
    @Mock
    private RoomAssignmentMapper assignmentMapper;

    @InjectMocks
    private RoomBoardService boardService;

    private List<Floor> mockFloors;
    private List<Room> mockRooms;
    private List<RoomType> mockTypes;

    @BeforeEach
    void setUp() {
        Floor floor1 = new Floor();
        floor1.setId(1L);
        floor1.setName("1楼");
        floor1.setFloorNo(1);
        mockFloors = Arrays.asList(floor1);

        RoomType type1 = new RoomType();
        type1.setId(1L);
        type1.setName("豪华大床房");
        type1.setCode("DELUXE_KING");
        type1.setIcon("king_bed");
        mockTypes = Arrays.asList(type1);

        Room room1 = new Room();
        room1.setId(1L);
        room1.setRoomNumber("101");
        room1.setFloorId(1L);
        room1.setRoomTypeId(1L);
        room1.setStatus(RoomStatus.CLEAN.getCode());

        Room room2 = new Room();
        room2.setId(2L);
        room2.setRoomNumber("102");
        room2.setFloorId(1L);
        room2.setRoomTypeId(1L);
        room2.setStatus(RoomStatus.OCCUPIED.getCode());

        mockRooms = Arrays.asList(room1, room2);
    }

    @Test
    void getBoardData_noFilter_returnsAllRooms() {
        when(floorMapper.selectList(any())).thenReturn(mockFloors);
        when(roomMapper.selectList(any())).thenReturn(mockRooms);
        when(roomTypeMapper.selectList(any())).thenReturn(mockTypes);
        when(assignmentMapper.selectList(any())).thenReturn(Collections.emptyList());

        RoomBoardVO result = boardService.getBoardData(null);

        assertNotNull(result);
        assertEquals(1, result.getFloors().size());
        assertEquals(2, result.getRooms().size());
        assertEquals("CLEAN", result.getRooms().get(0).getStatus());
        assertEquals("OCCUPIED", result.getRooms().get(1).getStatus());
    }

    @Test
    void getBoardData_statusSummary_correct() {
        when(floorMapper.selectList(any())).thenReturn(mockFloors);
        when(roomMapper.selectList(any())).thenReturn(mockRooms);
        when(roomTypeMapper.selectList(any())).thenReturn(mockTypes);
        when(assignmentMapper.selectList(any())).thenReturn(Collections.emptyList());

        RoomBoardVO result = boardService.getBoardData(null);

        assertEquals(1, result.getStatusSummary().get("CLEAN"));
        assertEquals(1, result.getStatusSummary().get("OCCUPIED"));
        assertEquals(0, result.getStatusSummary().get("DIRTY"));
    }

    @Test
    void getBoardData_occupancyRate_calculated() {
        when(floorMapper.selectList(any())).thenReturn(mockFloors);
        when(roomMapper.selectList(any())).thenReturn(mockRooms);
        when(roomTypeMapper.selectList(any())).thenReturn(mockTypes);
        when(assignmentMapper.selectList(any())).thenReturn(Collections.emptyList());

        RoomBoardVO result = boardService.getBoardData(null);

        // 1 occupied out of 2 rooms = 50%
        assertEquals(50.0, result.getOccupancyRate());
    }

    @Test
    void getBoardData_emptyRooms_returnsZeroRate() {
        when(floorMapper.selectList(any())).thenReturn(mockFloors);
        when(roomMapper.selectList(any())).thenReturn(Collections.emptyList());

        RoomBoardVO result = boardService.getBoardData(null);

        assertEquals(0.0, result.getOccupancyRate());
        assertTrue(result.getRooms().isEmpty());
    }
}
