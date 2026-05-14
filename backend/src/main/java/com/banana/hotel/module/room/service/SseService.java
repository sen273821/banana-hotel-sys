package com.banana.hotel.module.room.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter() {
        String id = String.valueOf(System.currentTimeMillis());
        SseEmitter emitter = new SseEmitter(0L);

        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError(e -> emitters.remove(id));

        emitters.put(id, emitter);
        return emitter;
    }

    public void broadcast(Long roomId, String roomNumber, String newStatus) {
        String data = String.format(
                "{\"roomId\":%d,\"roomNumber\":\"%s\",\"newStatus\":\"%s\"}",
                roomId, roomNumber, newStatus
        );

        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("room_status_changed")
                        .data(data));
            } catch (IOException e) {
                emitters.remove(id);
            }
        });
    }
}
