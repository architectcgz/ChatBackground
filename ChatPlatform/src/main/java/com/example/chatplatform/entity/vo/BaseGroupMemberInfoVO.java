package com.example.chatplatform.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
abstract class BaseGroupMemberInfoVO {
    /**
     * 成员的uid
     */
    public String memberId;

    /**
     * 群成员的身份 0:群成员 1:管理员 2:群主
     */
    public Integer memberType;
    /**
     * 群成员在群中的名称
     */
    public String memberAlias;
    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date joinTime;
}
