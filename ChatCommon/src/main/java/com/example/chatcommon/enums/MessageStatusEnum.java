package com.example.chatcommon.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageStatusEnum {
    UNSENT(0,"尚未发送"),
    SENDING(1,"发送中"),
    SENT(2, "送达"),
    FAIL(3,"发送失败"),
    READ(4,"已读"),
    UNREAD(5,"未读"),
    RECALL(4, "撤回");
    private final Integer code;

    private final String desc;


    public Integer getCode() {
        return this.code;
    }

    public String getDesc() {
        return desc;
    }
}
