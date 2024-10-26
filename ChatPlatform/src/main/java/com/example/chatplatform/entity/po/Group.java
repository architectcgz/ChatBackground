package com.example.chatplatform.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 群组表
 * @TableName group
 */
@Data
public class Group implements Serializable {
    /**
     * 群聊的uid
     */
    private String groupId;

    /**
     * 
     */
    private String groupName;

    private String groupAvatar;
    /**
     * 群主的id
     */
    private String leaderId;

    /**
     * 加群方式 0:直接加群 1:审核后加群
     */
    private Integer joinType;
    /*
     * 是否置顶 0: no 1:yes
     */
    private Boolean pinned;
    /**
     * 是否免打扰  0: no 1:yes
     */
    private Boolean muted;

    /**
     * 群状态 0:正常,1:群被解散,2:群被封禁
     */
    private Integer status;

    /**
     * 建群时间
     */
    private Date createTime;

    /**
     * 
     */

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}