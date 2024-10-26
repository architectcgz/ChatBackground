package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum JoinTypeEnum {
    DIRECT(0,"直接加群"),
    NEED_AGREE(1,"需要同意");
    private final Integer joinTypeCode;
    private final String desc;

    public String getDesc() {
        return desc;
    }

    public Integer getJoinTypeCode() {
        return joinTypeCode;
    }
    public static JoinTypeEnum getByJoinTypeCode(Integer code){
        for(JoinTypeEnum j : JoinTypeEnum.values()){
            if(j.joinTypeCode.equals(code)){
                return j;
            }
        }
        return null;
    }

}
