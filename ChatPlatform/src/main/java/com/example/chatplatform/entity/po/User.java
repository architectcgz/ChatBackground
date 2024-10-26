package com.example.chatplatform.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 * @author archi
 * @TableName user_info
 */
@Data
public class User implements Serializable {
    private Long id;
    private String refreshToken;
    private String avatar;
    private String qrCode;
    private Integer terminal;
    @JsonIgnore
    private String accessToken;
    /**
     *
     */
    @NotNull(message="userId不能为空")
    private String userId;
    /**
     *
     */
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    private String email;

    private String phone;
    /**
     * 昵称
     */
    @Size(max= 10,message="编码长度不能超过20")
    @Length(max= 10,message="编码长度不能超过20")
    private String nickname;
    /**
     * 0: 直接加,1: 同意后加群
     */
    private Integer joinType;
    /*
     * 0:直接加好友,1:同意后加好友
     */
    private Integer addType;
    /*
     * 0:女,1:男
     */
    private Integer gender;
    /*
     * 0:普通用户,1:特权用户
     */
    private Integer userType;
    /**
     * 密码
     */
    @Size(max= 32,message="编码长度不能超过32")
    @Length(max= 32,message="编码长度不能超过32")
    private String password;
    /**
     * 个性签名
     */
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    private String signature;
    /**
     * 状态 0:正常 1:禁用
     */

    private Integer status;
    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;
    /**
     * 地区
     */
    @Size(max= 50,message="编码长度不能超过50")
    @Length(max= 50,message="编码长度不能超过50")
    private String location;

    /**
     * 最后离开时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastOffTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
