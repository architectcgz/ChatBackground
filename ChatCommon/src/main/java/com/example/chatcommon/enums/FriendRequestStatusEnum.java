package com.example.chatcommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendRequestStatusEnum {
    UNHANDLED(0,"未处理"),
    EXPIRED(-1,"已过期"),
    ACCEPTED(1,"已同意");

    private final Integer code;
    private final String description;
    public static FriendRequestStatusEnum fromCode(Integer code){
        for(FriendRequestStatusEnum e: values()){
            if(e.code.equals(code)){
                return e;
            }
        }
        return null;
    }

}
