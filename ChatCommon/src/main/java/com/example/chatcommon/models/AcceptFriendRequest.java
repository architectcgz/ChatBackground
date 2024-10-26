package com.example.chatcommon.models;

import lombok.Data;

/**
 * @author archi
 */
@Data
public class AcceptFriendRequest {
    private String acceptUserId;
    private String requestUserId;
}
