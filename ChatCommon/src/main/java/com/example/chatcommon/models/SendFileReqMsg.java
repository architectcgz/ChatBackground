package com.example.chatcommon.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SendFileReqMsg {
    private String senderId;
    private String receiverId;
    private String fileListHashCode;
    private List<Map<String,String>> fileList;
}
