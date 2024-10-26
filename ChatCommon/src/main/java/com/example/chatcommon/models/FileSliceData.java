package com.example.chatcommon.models;

import lombok.Data;

@Data
public class FileSliceData {
    private String fileName;
    private String senderId;
    private String data;
    private Integer chunkIndex;
    private Integer totalChunks;
}
