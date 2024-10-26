package com.example.chatcommon.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NettyCmdType {
    TOKEN_EXPIRED(0,"token过期"),
    CONNECT(1, "第一次(或重连)初始化连接"),

    HEART_BEAT(2, "心跳"),

    FORCE_LOGOUT(3, "强制下线"),

    PRIVATE_MESSAGE(4, "私聊消息"),

    GROUP_MESSAGE(5, "群发消息"),

    FRIEND_REQUEST(6,"好友请求"),
    GROUP_REQUEST(7,"加群请求"),
    ACCEPT_FRIEND_REQUEST(8,"接受好友请求"),
    ACCEPT_GROUP_REQUEST(9,"接受群组请求"),
    SEND_FILE_REQUEST(10,"请求向好友发送文件"),
    ACCEPT_FILE_REQUEST(11,"接受好友的文件请求"),
    FILE_SLICE(12,"文件分片");
    private final Integer code;

    private final String desc;

    public static NettyCmdType fromCode(Integer code) {
        for (NettyCmdType typeEnum : values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }


    public Integer getCode() {
        return this.code;
    }

    public String getDesc() {
        return desc;
    }
}
