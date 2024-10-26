package com.example.chatcommon.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author archi
 */
@Data
@AllArgsConstructor
public class RequestFriendInfo {
    private String friendUid;
    private String friendAvatar;
    private String friendNickname;
    private Integer friendGender;
    private String requestMessage;
    private String location;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
