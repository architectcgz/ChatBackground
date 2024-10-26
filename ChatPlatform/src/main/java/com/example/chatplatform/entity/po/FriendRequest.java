package com.example.chatplatform.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * @author archi
 */
@Data
public class FriendRequest {
    private String requestUserId;
    private String friendId;
    private String requestMessage;
    private String alias;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
