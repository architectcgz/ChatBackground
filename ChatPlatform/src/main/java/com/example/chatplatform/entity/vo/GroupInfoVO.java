package com.example.chatplatform.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class GroupInfoVO {
    /**
     * 群聊的uid
     */
    private String groupId;

    /**
     *
     */
    private String groupName;
    /*
        群头像
     */
    private String groupAvatar;
    /**
     * 群主的id
     */
    private String leaderId;

    /**
     * 加群方式 0:直接加群 1:审核后加群
     */
    private Integer joinType;

    /**
     * 群状态 0:正常,1:群被解散,2:群被封禁
     */
    private Integer status;

    /**
     * 建群时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
