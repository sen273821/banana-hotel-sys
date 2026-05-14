package com.banana.hotel.module.auth.dto;

import lombok.Data;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private Integer role;
}
