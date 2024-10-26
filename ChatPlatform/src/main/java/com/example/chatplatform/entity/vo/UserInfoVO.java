package com.example.chatplatform.entity.vo;


import com.example.chatplatform.entity.constants.FormatMessage;
import com.example.chatplatform.entity.constants.RegexPatterns;
import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author archi
 */
@Data
public class UserInfoVO {
    private String userId;
    @Pattern(regexp = RegexPatterns.PHONE_REGEX,message = FormatMessage.PHONE_FORMAT_ERROR)
    private String phone;
    @Pattern(regexp = RegexPatterns.EMAIL_REGEX,message = FormatMessage.EMAIL_FORMAT_ERROR)
    private String email;
    private String avatar;
    private String qrCode;
    @Length(max = 10,message = FormatMessage.NICKNAME_LENGTH_ERROR)
    private String nickname;
    private Integer status;
    private String location;
    private Integer joinType;
    private Integer gender;
    @Length(max = 20,message = FormatMessage.SIGNATURE_FORMAT_ERROR)
    private String signature;
    private Integer userType;
    private Integer addType;
    private Date createTime;
    private Date updateTime;
}
