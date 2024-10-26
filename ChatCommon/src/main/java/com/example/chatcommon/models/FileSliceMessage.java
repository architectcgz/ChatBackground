package com.example.chatcommon.models;

import lombok.Data;

@Data
public class FileSliceMessage {
    private String fileName;
    private String senderId;
    private String receiverId;
    private String data;
    private Integer chunkIndex;
    private Integer totalChunks;
    private Boolean sendResult;
    /*
            "senderId": senderId,
        "receiverId": receiverId,
        'data': base64Encode(fileData.sublist(start, end)),
        'chunkIndex': chunkIndex,
        'totalChunks': totalChunks,
        "sendResult": true,
     */
}
