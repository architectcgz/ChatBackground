package com.example.chatplatform.entity.dto;

import lombok.Data;

/**
 * @author archi
 */
@Data
public class UserInfoDTO {
    private String userId;
    private String phone;
    private String email;
    private String avatar;
    private String nickname;
    private Integer status;
    private String location;
    private Integer joinType;
    private Integer gender;
    private String signature;
    private Integer userType;
    private Integer addType;
}
