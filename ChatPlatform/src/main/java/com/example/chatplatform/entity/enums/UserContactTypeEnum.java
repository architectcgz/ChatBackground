package com.example.chatplatform.entity.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum UserContactTypeEnum {
    USER(0,"U","好友"),
    GROUP(1,"G","群聊");
    private Integer type;
    private String prefix;
    private String desc;

    public Integer getType(){
        return type;
    }
    public String getPrefix(){
        return prefix;
    }
    public String getDesc(){
        return desc;
    }
    public static UserContactTypeEnum getByName(String name){
        try {
            if(StrUtil.isBlank(name)){
                return null;
            }
            return UserContactTypeEnum.valueOf(name.toUpperCase());
        }catch (Exception e){
            return null;
        }
    }
    //如前缀为U1212341 通过这个方法可以看出是好友类型
    public static UserContactTypeEnum getByPrefix(String prefix){
        try {
            if(StrUtil.isBlank(prefix)){
                return null;
            }
            prefix = prefix.substring(0,1);
            for(UserContactTypeEnum typeEnum:UserContactTypeEnum.values()){
                if(prefix.equals(typeEnum.getPrefix())){
                    return typeEnum;
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }

}
