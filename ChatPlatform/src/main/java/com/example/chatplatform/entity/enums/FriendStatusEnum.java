package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum FriendStatusEnum {
    PENDING(0,"用户向对方发送的好友请求待处理"),
    ACCEPTED(1,"双方已成为好友"),
    REJECTED(2,"用户的好友请求被拒绝"),
    BLOCKED(3,"用户拉黑了对方"),
    UNFRIENDED(4,"用户删除了对方"),
    BLOCKED_AND_UNFRIENDED(5,"用户拉黑且删除了对方");

    private final Integer code;
    private final String desc;
    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    public FriendStatusEnum getByStatus(String status){
        for(FriendStatusEnum friendStatusEnum:FriendStatusEnum.values()){
            if(status.equals(friendStatusEnum.getCode())){
                return friendStatusEnum;
            }
        }
        return null;
    }
}
