package com.example.chatcommon.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */

@AllArgsConstructor
public enum SendResultEnum {

    SUCCESS(0, "发送成功"),

    RECEIVER_NOT_ONLINE(1, "对方当前不在线"),

    RECEIVER_CHANNEL_NOT_FOUND(2, "未找到对方的channel"),

    FAIL(1000,"由于未知错误发送失败");

    private final Integer code;
    private final String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
