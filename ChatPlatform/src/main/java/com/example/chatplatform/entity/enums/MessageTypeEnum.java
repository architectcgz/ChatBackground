package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageTypeEnum {
    TEXT(0,"文本"),
    IMAGE(1,"图片"),
    FILE(2,"文件"),
    VIDEO(3,"视频"),
    VOICE(4,"语音"),
    RECALL(5,"撤回消息");


    private final Integer code;
    private final String desc;

    public static MessageTypeEnum getByType(Integer type) {
        for(MessageTypeEnum messageType:MessageTypeEnum.values()){
            if(messageType.getCode().equals(type)){
                return messageType;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}
