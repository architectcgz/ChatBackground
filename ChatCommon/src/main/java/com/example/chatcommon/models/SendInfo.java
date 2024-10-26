package com.example.chatcommon.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendInfo<T>implements Serializable{
    private static final Long serialVersionUID = 1L;
    /**
     * 命令 0: 初始化连接 1: 心跳 2:强制下线 3:私聊消息 4:群聊消息 5:好友请求信息
     */
    private Integer cmd;

    /**
     * 推送消息体
     */
    private T data;
}
