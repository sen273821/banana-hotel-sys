package com.banana.hotel.module.room.service;

import com.banana.hotel.common.enums.RoomStatus;
import com.banana.hotel.module.room.entity.Room;
import com.banana.hotel.module.room.entity.RoomStatusLog;
import com.banana.hotel.module.room.mapper.RoomMapper;
import com.banana.hotel.module.room.mapper.RoomStatusLogMapper;
import com.banana.hotel.module.room.vo.RoomStatusLogVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomStatusService {

    private final RoomMapper roomMapper;
    private final RoomStatusLogMapper logMapper;
    private final SseService sseService;

    private static final Map<RoomStatus, Set<RoomStatus>> TRANSITIONS = new EnumMap<>(RoomStatus.class);

    static {
        TRANSITIONS.put(RoomStatus.CLEAN, EnumSet.of(
                RoomStatus.RESERVED, RoomStatus.BOOKED,
                RoomStatus.OCCUPIED, RoomStatus.OUT_OF_SERVICE
        ));
        TRANSITIONS.put(RoomStatus.RESERVED, EnumSet.of(
                RoomStatus.CLEAN, RoomStatus.OUT_OF_SERVICE
        ));
        TRANSITIONS.put(RoomStatus.BOOKED, EnumSet.of(
                RoomStatus.OCCUPIED, RoomStatus.CLEAN
        ));
        TRANSITIONS.put(RoomStatus.OCCUPIED, EnumSet.of(
                RoomStatus.DIRTY
        ));
        TRANSITIONS.put(RoomStatus.DIRTY, EnumSet.of(
                RoomStatus.CLEAN
        ));
        TRANSITIONS.put(RoomStatus.OUT_OF_SERVICE, EnumSet.of(
                RoomStatus.CLEAN
        ));
    }

    @Transactional
    public void changeStatus(Long roomId, String newStatusStr, String reason) {
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new NoSuchElementException("房间不存在: " + roomId);
        }

        RoomStatus current = RoomStatus.fromCode(room.getStatus());
        RoomStatus target = RoomStatus.valueOf(newStatusStr);

        validateTransition(current, target);

        room.setStatus(target.getCode());
        roomMapper.updateById(room);

        saveLog(roomId, current, target, reason);
        sseService.broadcast(roomId, room.getRoomNumber(), target.name());
    }

    public List<RoomStatusLogVO> getLogs(Long roomId, int page, int size) {
        Page<RoomStatusLog> pageParam = new Page<>(page, size);
        Page<RoomStatusLog> result = logMapper.selectPage(pageParam,
                new LambdaQueryWrapper<RoomStatusLog>()
                        .eq(RoomStatusLog::getRoomId, roomId)
                        .orderByDesc(RoomStatusLog::getCreatedAt)
        );

        return result.getRecords().stream().map(log -> {
            RoomStatusLogVO vo = new RoomStatusLogVO();
            vo.setId(log.getId());
            vo.setPreviousStatus(RoomStatus.fromCode(log.getPreviousStatus()).name());
            vo.setNewStatus(RoomStatus.fromCode(log.getNewStatus()).name());
            vo.setOperatorName(log.getOperatorName());
            vo.setReason(log.getReason());
            vo.setCreatedAt(log.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    private void validateTransition(RoomStatus from, RoomStatus to) {
        Set<RoomStatus> allowed = TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new IllegalStateException(
                    String.format("不允许从[%s]转换到[%s]", from.getLabel(), to.getLabel())
            );
        }
    }

    private void saveLog(Long roomId, RoomStatus prev, RoomStatus next, String reason) {
        RoomStatusLog log = new RoomStatusLog();
        log.setRoomId(roomId);
        log.setPreviousStatus(prev.getCode());
        log.setNewStatus(next.getCode());
        log.setOperatorName("系统管理员");
        log.setReason(reason);
        logMapper.insert(log);
    }
}
