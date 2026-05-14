package com.banana.hotel.module.room.service;

import com.banana.hotel.common.enums.RoomStatus;
import com.banana.hotel.module.room.entity.Room;
import com.banana.hotel.module.room.entity.RoomStatusLog;
import com.banana.hotel.module.room.mapper.RoomMapper;
import com.banana.hotel.module.room.mapper.RoomStatusLogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomStatusServiceTest {

    @Mock
    private RoomMapper roomMapper;
    @Mock
    private RoomStatusLogMapper logMapper;
    @Mock
    private SseService sseService;

    @InjectMocks
    private RoomStatusService statusService;

    @Test
    void changeStatus_validTransition_success() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus(RoomStatus.CLEAN.getCode());

        when(roomMapper.selectById(1L)).thenReturn(room);
        when(roomMapper.updateById(any())).thenReturn(1);
        when(logMapper.insert(any())).thenReturn(1);

        statusService.changeStatus(1L, "OCCUPIED", "客人入住");

        assertEquals(RoomStatus.OCCUPIED.getCode(), room.getStatus());
        verify(sseService).broadcast(1L, "101", "OCCUPIED");
    }

    @Test
    void changeStatus_invalidTransition_throwsException() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus(RoomStatus.DIRTY.getCode());

        when(roomMapper.selectById(1L)).thenReturn(room);

        assertThrows(IllegalStateException.class, () ->
                statusService.changeStatus(1L, "OCCUPIED", "非法操作")
        );
    }

    @Test
    void changeStatus_roomNotFound_throwsException() {
        when(roomMapper.selectById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
                statusService.changeStatus(99L, "CLEAN", "")
        );
    }

    @Test
    void changeStatus_logsStatusChange() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus(RoomStatus.OCCUPIED.getCode());

        when(roomMapper.selectById(1L)).thenReturn(room);
        when(roomMapper.updateById(any())).thenReturn(1);
        when(logMapper.insert(any())).thenReturn(1);

        statusService.changeStatus(1L, "DIRTY", "客人退房");

        ArgumentCaptor<RoomStatusLog> captor = ArgumentCaptor.forClass(RoomStatusLog.class);
        verify(logMapper).insert(captor.capture());

        RoomStatusLog log = captor.getValue();
        assertEquals(RoomStatus.OCCUPIED.getCode(), log.getPreviousStatus());
        assertEquals(RoomStatus.DIRTY.getCode(), log.getNewStatus());
        assertEquals("客人退房", log.getReason());
    }

    @Test
    void changeStatus_cleanToReserved_valid() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus(RoomStatus.CLEAN.getCode());

        when(roomMapper.selectById(1L)).thenReturn(room);
        when(roomMapper.updateById(any())).thenReturn(1);
        when(logMapper.insert(any())).thenReturn(1);

        assertDoesNotThrow(() ->
                statusService.changeStatus(1L, "RESERVED", "团队预留")
        );
    }

    @Test
    void changeStatus_dirtyToClean_valid() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus(RoomStatus.DIRTY.getCode());

        when(roomMapper.selectById(1L)).thenReturn(room);
        when(roomMapper.updateById(any())).thenReturn(1);
        when(logMapper.insert(any())).thenReturn(1);

        statusService.changeStatus(1L, "CLEAN", "清洁完成");

        assertEquals(RoomStatus.CLEAN.getCode(), room.getStatus());
    }

    @Test
    void changeStatus_outOfServiceToClean_valid() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setStatus(RoomStatus.OUT_OF_SERVICE.getCode());

        when(roomMapper.selectById(1L)).thenReturn(room);
        when(roomMapper.updateById(any())).thenReturn(1);
        when(logMapper.insert(any())).thenReturn(1);

        statusService.changeStatus(1L, "CLEAN", "维修完成");

        assertEquals(RoomStatus.CLEAN.getCode(), room.getStatus());
    }
}
