package com.example.chatplatform.entity.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 群组加入请求表
 * @TableName group_request
 */
@Data
public class GroupRequest implements Serializable {
    /**
     * 
     */
    private String groupId;

    /**
     * 请求者的uid
     */
    private String userId;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 加群请求状态 Pending:待处理 Approved:同意进群 Rejected:拒绝进群
     */
    private Object status;

    /**
     * 处理者(管理员或群主)的id
     */
    private String handlerId;

    private static final long serialVersionUID = 1L;
}