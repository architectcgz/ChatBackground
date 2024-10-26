package com.example.chatplatform.entity.enums;

import lombok.AllArgsConstructor;

/**
 * @author archi
 */
@AllArgsConstructor
public enum GroupRequestStatusEnum {
    PENDING("Pending","待处理"),
    APPROVED("Approved","请求通过"),
    REJECTED("Rejected","请求被拒绝")
    ;
    private final String status;
    private final String desc;

    public String getDesc() {
        return desc;
    }

    public String getStatus() {
        return status;
    }
}
