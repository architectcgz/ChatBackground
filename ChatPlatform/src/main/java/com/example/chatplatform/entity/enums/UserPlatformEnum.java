package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author archi
 */
@AllArgsConstructor
public enum UserPlatformEnum {

    WEB(0,"网页端"),
    PC(1,"PC端"),
    APP(2,"移动端");

    private final Integer code;
    private final String desc;

    public static UserPlatformEnum fromCode(Integer code) {
        for (UserPlatformEnum typeEnum : values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    public static List<Integer> codes() {
        return Arrays.stream(values()).map(UserPlatformEnum::getCode).collect(Collectors.toList());
    }
}
