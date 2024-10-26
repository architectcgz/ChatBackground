package com.example.chatcommon.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author archi
 */

@AllArgsConstructor
public enum TerminalType {
    WEB(0,"网页端"),
    PC(1,"PC端"),
    APP(2,"移动端");

    private final Integer code;
    private final String desc;

    public static TerminalType fromCode(Integer code) {
        for (TerminalType typeEnum : values()) {
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
    public static Set<Integer> codes() {
        return Arrays.stream(values()).map(TerminalType::getCode).collect(Collectors.toSet());
    }
}
