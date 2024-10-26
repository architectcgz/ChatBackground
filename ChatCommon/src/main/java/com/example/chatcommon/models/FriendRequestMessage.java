package com.example.chatcommon.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author archi
 */
@Data
public class FriendRequestMessage implements Serializable {
    String requestUserId;
    int terminal;
    String friendUid;
    String nickname;
    String avatar;
    Integer gender;
    String location;
    String requestMessage;
    Integer status;
    Date createTime;
    Date updateTime;
}
