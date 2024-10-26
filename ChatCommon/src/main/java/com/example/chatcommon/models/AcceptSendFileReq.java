package com.example.chatcommon.models;

import lombok.Data;

@Data
public class AcceptSendFileReq {
    private String sendUserId;
    private String acceptUserId;
    private String fileListHashCode;
}
