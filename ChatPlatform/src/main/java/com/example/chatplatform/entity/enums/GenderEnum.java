package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum GenderEnum {
    MALE(0,"女"),
    FEMALE(1,"男");
    private Integer genderCode;
    private String description;

    public final Integer getGenderCode() {
        return genderCode;
    }

    public final String getDesc() {
        return description;
    }

    public static GenderEnum getDescByCode(Integer code){
        for (GenderEnum genderEnum : values()) {
            if (genderEnum.getGenderCode().equals(code)) {
                return genderEnum;
            }
        }
        return null;
    }
}
