package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatusEnum {
    ENABLE(0,"启用"),
    BANNED(1,"禁用");
    private Integer statusCode;
    private String desc;
    public Integer getStatusCode() {
        return statusCode;
    }

    public String getDesc() {
        return desc;
    }

    public static UserStatusEnum getByStatusCode(Integer statusCode){
        for(UserStatusEnum u : UserStatusEnum.values()){
            if(statusCode.equals(u.getStatusCode())){
                return u;
            }
        }
        return null;
    }
}
