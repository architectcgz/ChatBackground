package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum UserTypeEnum {
    NORMAL_USER(0,"USER","普通用户"),
    VIP_USER(1,"VIP","特权用户");
    private Integer userTypeCode;
    private String userType;
    private String description;

    public String getUserType() {
        return userType;
    }

    public Integer getUserTypeCode() {
        return userTypeCode;
    }

    public String getDesc() {
        return description;
    }
    public UserTypeEnum getByUserType(String userType){
        try {
            for(UserTypeEnum userTypeEnum:UserTypeEnum.values()){
                if(userType.equals(userTypeEnum.userType)){
                    return userTypeEnum;
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
    public static UserTypeEnum getByUserTypeCode(Integer userTypeCode){
        try {
            for(UserTypeEnum userTypeEnum:UserTypeEnum.values()){
                if(userTypeCode.equals(userTypeEnum.userTypeCode)){
                    return userTypeEnum;
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String toString() {
        return "UserTypeCode: "+userTypeCode+", UserType: "+userType+", Description: "+description;
    }
}
