package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum GroupMemberTypeEnum {
    MEMBER(0,"成员"),
    ADMIN(1,"管理员"),
    LEADER(2,"群主");
    private final Integer member_type;
    private final String desc;

    public String getDesc() {
        return desc;
    }

    public Integer getMemberType() {
        return member_type;
    }
}
