package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum GroupStatusEnum {
    NORMAL(0,"群聊状态正常"),
    DISMISSED(1,"群聊被解散"),
    BANNED(2,"群聊被封禁");

    private final Integer status;
    private final String desc;

    public String getDesc() {
        return desc;
    }

    public Integer getStatus() {
        return status;
    }
}
