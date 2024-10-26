package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum GroupJoinTypeEnum {
    DIRECT(0,"直接加入"),
    REVIEW(1,"需要审核");
    private final Integer groupJoinType;
    private final String desc;

    public String getDesc() {
        return desc;
    }

    public Integer getGroupJoinType() {
        return groupJoinType;
    }
}
