package com.example.chatcommon.models;

import lombok.Data;

@Data
public class SendResult<T> {
    /**
     * 发送方
     */
    private UserInfo sender;

    /**
     * 接收方
     */
    private String receiverId;

    /**
     * 发送状态 SendResultEnum
     */
    private Integer code;

    /**
     * 消息内容
     */
    private T data;

}
