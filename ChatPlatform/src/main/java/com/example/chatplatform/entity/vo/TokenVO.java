package com.example.chatplatform.entity.vo;

import lombok.Data;

@Data
public class TokenVO {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
