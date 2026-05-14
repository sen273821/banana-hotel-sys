package com.banana.hotel.common.enums;

import lombok.Getter;

@Getter
public enum RoomStatus {

    CLEAN(0, "空净"),
    RESERVED(1, "预留"),
    BOOKED(2, "预订"),
    OCCUPIED(3, "在住"),
    DIRTY(4, "脏房"),
    OUT_OF_SERVICE(5, "停售");

    private final int code;
    private final String label;

    RoomStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static RoomStatus fromCode(int code) {
        for (RoomStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid room status code: " + code);
    }
}
