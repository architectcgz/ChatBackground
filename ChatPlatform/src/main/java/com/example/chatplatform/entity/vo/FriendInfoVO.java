package com.example.chatplatform.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author archi
 */
@Data
public class FriendInfoVO {
    private String userId;
    private String phone;
    private String email;
    private String avatar;
    private String alias;
    private String nickname;
    /**
     * 是否置顶
     * 1 yes 0 no
     */
    private Boolean pinned;
    /**
     * 是否免打扰
     * 1 yes 0 no
     */
    private Boolean muted;
    private String location;
    private Integer gender;
    private String signature;
    private Date createTime;
    private Date updateTime;
}
