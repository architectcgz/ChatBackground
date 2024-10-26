package com.example.chatplatform.util;

import com.example.chatplatform.security.CustomUserDetails;
import com.example.chatplatform.entity.CustomException;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.entity.po.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author archi
 */
@Slf4j
public class SecurityContextUtils {
    public static UserDetails getLocalUserDetail(){
        Object userPrinciple = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrinciple instanceof UserDetails){
            return (UserDetails) userPrinciple;
        }else{
            log.info("上下文中没有userDetail对象");
            return null;
        }
    }
    public static User getUser(){
        Object userPrinciple = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrinciple instanceof UserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) userPrinciple;
            return userDetails.getUser();
        }else{
            log.info("上下文中没有userDetail对象");
            return null;
        }
    }
    public static User getUserNotNull(){
        User user = getUser();
        if(null==user){
            log.info("这里出现了问题，user为null");
            throw new CustomException(ResponseEnum.USER_AUTH_ERROR.getCode(),ResponseEnum.USER_AUTH_ERROR.getMessage());
        }
        return user;
    }
}

