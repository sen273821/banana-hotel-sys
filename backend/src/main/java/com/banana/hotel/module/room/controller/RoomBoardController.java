package com.banana.hotel.module.room.controller;

import com.banana.hotel.common.Result;
import com.banana.hotel.module.room.dto.RoomStatusChangeDTO;
import com.banana.hotel.module.room.service.RoomBoardService;
import com.banana.hotel.module.room.service.RoomStatusService;
import com.banana.hotel.module.room.service.SseService;
import com.banana.hotel.module.room.vo.RoomBoardVO;
import com.banana.hotel.module.room.vo.RoomStatusLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomBoardController {

    private final RoomBoardService boardService;
    private final RoomStatusService statusService;
    private final SseService sseService;

    @GetMapping("/board")
    public Result<RoomBoardVO> getBoardData(
            @RequestParam(required = false) Long floorId) {
        return Result.success(boardService.getBoardData(floorId));
    }

    @PutMapping("/{roomId}/status")
    public Result<Void> changeStatus(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomStatusChangeDTO dto) {
        try {
            statusService.changeStatus(roomId, dto.getNewStatus(), dto.getReason());
            return Result.success(null);
        } catch (NoSuchElementException e) {
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/{roomId}/logs")
    public Result<List<RoomStatusLogVO>> getLogs(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(statusService.getLogs(roomId, page, size));
    }

    @GetMapping(value = "/board/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseService.createEmitter();
    }
}
