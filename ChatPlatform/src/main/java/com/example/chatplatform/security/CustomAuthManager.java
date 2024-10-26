package com.example.chatplatform.security;

import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author archi
 */
@Slf4j
public class CustomAuthManager implements AuthenticationManager {
    public CustomAuthManager(PasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService){
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
    }
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //获取到身份和密码
        String userId = (String) authentication.getPrincipal();
        String pwd = (String) authentication.getCredentials();
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId);
        User user = customUserDetails.getUser();
        //注意这里要使用BCryptEncoder的match方法，不然每次加密结构都是不同的，密码都是错误!
        if(!passwordEncoder.matches(pwd,user.getPassword())){
            throw new BadCredentialsException("用户密码错误");
        }
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        if(user.getUserType()==0){//普通用户
            grantedAuths.add(new SimpleGrantedAuthority(SystemConstants.NORMAL_USER));
        }else{//特权用户
            grantedAuths.add(new SimpleGrantedAuthority(SystemConstants.VIP_USER));
        }
        log.info("密码正确,返回UsernamePasswordAuthenticationToken");

        return new UsernamePasswordAuthenticationToken(customUserDetails,null, customUserDetails.getAuthorities());
    }
}
