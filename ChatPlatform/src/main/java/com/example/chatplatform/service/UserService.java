package com.example.chatplatform.service;

import com.example.chatplatform.entity.dto.UserInfoDTO;
import com.example.chatplatform.entity.dto.login.LoginDTO;
import com.example.chatplatform.entity.dto.register.EmailRegisterDTO;
import com.example.chatplatform.entity.dto.register.RegisterDTO;
import com.example.chatplatform.entity.vo.TokenVO;
import com.example.chatplatform.entity.vo.UserInfoVO;

import java.util.Date;

/**
 * @author archi
 */
public interface UserService {

    TokenVO login(LoginDTO loginDTO);

    void logout();

    TokenVO refreshToken(String refreshToken);

    UserInfoVO getUserInfo();

    void register(RegisterDTO registerDTO);

    void updateUserInfo(UserInfoDTO userInfoDTO);

    String getQRCode();

    Date getUserUpdateTime();
}
