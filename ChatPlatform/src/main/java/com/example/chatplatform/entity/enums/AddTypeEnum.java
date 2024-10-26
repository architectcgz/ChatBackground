package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum AddTypeEnum {
    DIRECT(0,"直接添加好友"),
    NEED_AGREE(1,"同意后添加好友");
    private final Integer code;
    private final String desc;

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
    public static AddTypeEnum getByCode(Integer code){
        for(AddTypeEnum a:AddTypeEnum.values()){
            if(code.equals(a.code)){
                return a;
            }
        }
        return null;
    }
}
