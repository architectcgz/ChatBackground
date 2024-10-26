package com.example.chatplatform.entity.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author archi
 */
@Data
public class FriendRequestVO {
    private String friendUid;
    private String friendAvatar;
    private String friendNickname;
    private Integer friendGender;
    private String requestMessage;
    private String location;
    private Integer status;
    private Date createTime;//时间戳
    private Date updateTime;
}
