package com.example.chatplatform.entity.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友列表
 * @TableName friend
 */
@Data
public class Friend implements Serializable {
    /**
     * 用户的id,用于指定是谁的好友列表
     */
    private String userId;

    /**
     * 好友的id
     */
    private String friendId;
    /**
     * 对好友的备注
     */
    private String alias;

    /**
     好友状态 Pending: 用户发起的好友申请待处理 Accepted:双方已成为好友 Rejected:用户的好友申请被拒绝
     Blocked：用户拉黑了对方Unfriended: 用户删除了好友关系
     BlockedAndUnfriended: 用户拉黑且删除了对方
     */
    private Integer status;

    //是否置顶
    private Boolean pinned;

    //是否免打扰
    private Boolean muted;
    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}