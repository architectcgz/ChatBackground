package com.example.chatplatform.security;

import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.constants.RegexPatterns;
import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.mapper.UserMapper;
import com.example.chatplatform.util.JsonUtils;
import com.example.chatplatform.util.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author archi
 */

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (isValidEmail(username)) {
            return loadUserByEmail(username);
        } else if (isValidPhone(username)) {
            return loadUserByPhone(username);
        }else if(isValidUserId(username)){
            return loadUserByUserId(username);
        }
        else {
            throw new UsernameNotFoundException("用户名不合法: " + username);
        }
    }

    private UserDetails loadUserByUserId(String username) {
        String key = RedisKeys.USER_INFO_ID + username;
        log.info("loadUserByUserId: "+key);
        String jsonUser = RedisUtils.get(key);
        User user = null;
        if (null != jsonUser) {
            user = JsonUtils.jsonStrToJavaObj(jsonUser, User.class);
        }
        if (null == user) {
            user = userMapper.getUserInfoByUid(username);
            if (null == user) {
                throw new UsernameNotFoundException("Uid为 " + username + " 的用户不存在");
            }
            RedisUtils.storeBeanAsJson(key, user, SystemConstants.REDIS_ONE_WEEK_EXPIRATION);
        }

        return buildUserDetails(user);
    }

    private UserDetails loadUserByEmail(String email) {
        String key = RedisKeys.USER_LOGIN_EMAIL + email;
        String userId = RedisUtils.get(key);
        log.info("loadUserByEmail: "+userId);
        String jsonUser = RedisUtils.get(RedisKeys.USER_INFO_ID+userId);
        User user = null;

        if (null != jsonUser) {
            user = JsonUtils.jsonStrToJavaObj(jsonUser, User.class);
        }

        if (null == user) {
            user = userMapper.findByEmail(email);
            if (null == user) {
                throw new UsernameNotFoundException("邮箱为 " + email + " 的用户不存在");
            }
            RedisUtils.storeBeanAsJson(RedisKeys.USER_INFO_ID+user.getUserId(), user, SystemConstants.REDIS_ONE_WEEK_EXPIRATION);
        }

        return buildUserDetails(user);
    }

    private UserDetails loadUserByPhone(String phone) {
        String key = RedisKeys.USER_LOGIN_PHONE + phone;
        String userId = RedisUtils.get(key);
        String jsonUser = RedisUtils.get(RedisKeys.USER_INFO_ID+userId);
        User user = null;

        if (null != jsonUser) {
            user = JsonUtils.jsonStrToJavaObj(jsonUser, User.class);
        }

        if (null == user) {
            user = userMapper.findByPhone(phone);
            if (null == user) {
                throw new UsernameNotFoundException("手机号为 " + phone + " 的用户不存在");
            }
            RedisUtils.storeBeanAsJson(key, user, SystemConstants.REDIS_ONE_WEEK_EXPIRATION);
        }

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        // 1. commaSeparatedStringToAuthorityList放入角色时需要加前缀ROLE_，而在controller使用时不需要加ROLE_前缀
        // 2. 放入的是权限时，不能加ROLE_前缀，hasAuthority与放入的权限名称对应即可
        CustomUserDetails userDetails = null;
        if (user.getUserType() == 0) {
            userDetails = new CustomUserDetails(
                    user,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(SystemConstants.ROLE_NORMAL_USER),
                    true, true, true, true);
        } else {
            userDetails = new CustomUserDetails(
                    user,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(SystemConstants.ROLE_VIP_USER),
                    true, true, true, true);
        }
        return userDetails;
    }

    private boolean isValidEmail(String username) {
        // 邮箱验证
        return username.matches(RegexPatterns.EMAIL_REGEX);
    }

    private boolean isValidPhone(String username) {
        //手机号验证
        return username.matches(RegexPatterns.PHONE_REGEX);
    }

    private boolean isValidUserId(String userId){
        return userId.length()==12 && userId.startsWith("U");
    }
}
