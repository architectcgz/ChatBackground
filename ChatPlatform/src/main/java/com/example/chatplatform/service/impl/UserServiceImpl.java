package com.example.chatplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.example.chatcommon.utils.JwtUtils;
import com.example.chatplatform.entity.CustomRuntimeException;
import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.dto.UserInfoDTO;
import com.example.chatplatform.entity.dto.login.EmailLoginDTO;
import com.example.chatplatform.entity.dto.login.LoginDTO;
import com.example.chatplatform.entity.dto.login.PhoneLoginDTO;
import com.example.chatplatform.entity.dto.register.EmailRegisterDTO;
import com.example.chatplatform.entity.dto.register.PhoneRegisterDTO;
import com.example.chatplatform.entity.dto.register.RegisterDTO;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.entity.vo.TokenVO;
import com.example.chatplatform.entity.vo.UserInfoVO;
import com.example.chatplatform.mapper.FriendMapper;
import com.example.chatplatform.mapper.GroupMapper;
import com.example.chatplatform.mapper.UserMapper;
import com.example.chatplatform.security.CustomAuthManager;
import com.example.chatplatform.service.UserService;
import com.example.chatplatform.util.*;
import com.google.zxing.WriterException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Set;

/**
 * @author archi
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private FastDFSClient fastDFSClient;
    @Resource
    private FriendMapper friendMapper;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private CustomAuthManager customAuthManager;


    @Override
    public void register(RegisterDTO registerDTO) {
        User user = null;
        String captchaKey = null;
        if(registerDTO instanceof PhoneRegisterDTO){
            user = userMapper.findByPhone(((PhoneRegisterDTO) registerDTO).getPhone());
            captchaKey = RedisKeys.PHONE_CAPTCHA+((PhoneRegisterDTO) registerDTO).getPhone();
        }else if(registerDTO instanceof EmailRegisterDTO){
            user = userMapper.findByEmail(((EmailRegisterDTO) registerDTO).getEmail());
            captchaKey = RedisKeys.EMAIL_CAPTCHA+((EmailRegisterDTO) registerDTO).getEmail();
        }
        String captcha = registerDTO.getCaptcha();
        //先查找这个手机号是否注册过
        if(null== user){
            try {
                //没有注册过，那么新创建一个账号
                //先检查验证码是否正确
                String captchaRedis = RedisUtils.get(captchaKey);
                //大写验证码输入小写同样认为是正确的
                if (null == captcha || !captcha.equalsIgnoreCase(captchaRedis)) {
                    throw new CustomRuntimeException(ResponseEnum.CAPTCHA_VERIFY_ERROR.getCode(), ResponseEnum.CAPTCHA_VERIFY_ERROR.getMessage());
                }
                //删除验证码,防止多次注册
                RedisUtils.del(captchaKey);

                String userId = GenerateUtils.generateUserUid();
                //生成Uid后查数据库，防止Uid被使用过
                while (true){
                    User registedUser = userMapper.getUserInfoByUid(userId);
                    if(registedUser==null){
                        //通过新生成的uid找到的用户为null,说明该uid没有被使用过
                        break;
                    }else{
                        //该uid被使用过,重新生成一个新的Uid
                        userId = GenerateUtils.generateUserUid();
                    }
                }
                user = new User();
                user.setUserId(userId);
                user.setNickname(registerDTO.getNickname());
                if(registerDTO instanceof PhoneRegisterDTO){
                    user.setPhone(((PhoneRegisterDTO) registerDTO).getPhone());
                }else if(registerDTO instanceof EmailRegisterDTO){
                    user.setEmail(((EmailRegisterDTO) registerDTO).getEmail());
                }
                user.setStatus(0);
                user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
                user.setCreateTime(DateTime.now());
                user.setUpdateTime(DateTime.now());
                user.setUserType(0);
                user.setAddType(0);
                user.setJoinType(0);
                user.setAvatar("https://avatar.iran.liara.run/public/boy?username=Ash");
                //生成用户的二维码
                byte[] avatar = downloadAvatar("https://avatar.iran.liara.run/public/boy?username=Ash");
                byte[] qrCode = QRCodeUtils.generateQRCodeByte(userId,avatar,300,300);
                String result = fastDFSClient.uploadByteArray(qrCode,user.getUserId()+"_qrcode.png");
                if(result==null){
                    throw new CustomRuntimeException(ResponseEnum.QRCODE_UPLOAD_ERROR.getCode(),ResponseEnum.QRCODE_UPLOAD_ERROR.getMessage());
                }
                user.setQrCode(result);
                //保存到数据库和redis中,redis保存3天
                userMapper.register(user);
                RedisUtils.set(RedisKeys.USER_LOGIN_EMAIL+user.getEmail(),userId,SystemConstants.REDIS_THREE_DAYS_EXPIRATION);
                RedisUtils.storeBeanAsJson(RedisKeys.USER_INFO_ID + userId, user, SystemConstants.REDIS_THREE_DAYS_EXPIRATION);

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            //已经注册过了，那么提示已经注册过
            throw new CustomRuntimeException(ResponseEnum.ALREADY_REGISTERED.getCode(),ResponseEnum.ALREADY_REGISTERED.getMessage());
        }
    }

    @Override
    public void updateUserInfo(UserInfoDTO userInfoDTO) {
        User user = SecurityContextUtils.getUserNotNull();
        String userJson = RedisUtils.get(RedisKeys.USER_INFO_ID + user.getUserId());
        User userInRedis = JsonUtils.jsonStrToJavaObj(userJson, User.class);
        // 更新非 null 的字段
        if (userInfoDTO.getPhone() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getPhone(), userInRedis.getPhone())) {
            userInRedis.setPhone(userInfoDTO.getPhone());
        }
        if (userInfoDTO.getEmail() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getEmail(), userInRedis.getEmail())) {
            userInRedis.setEmail(userInfoDTO.getEmail());
        }
        if (userInfoDTO.getAvatar() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getAvatar(), userInRedis.getAvatar())) {
            userInRedis.setAvatar(userInfoDTO.getAvatar());
            //如果用户修改了头像,那么二维码也要修改
            try {
                byte[] avatar = downloadAvatar(userInfoDTO.getAvatar());
                byte[] newQRCode = QRCodeUtils.generateQRCodeByte(user.getUserId(),avatar,300,300);
                String result = fastDFSClient.uploadByteArray(newQRCode,user.getUserId()+"_qrcode.png");
                if(result==null){
                    throw new CustomRuntimeException(ResponseEnum.QRCODE_UPLOAD_ERROR.getCode(),ResponseEnum.QRCODE_UPLOAD_ERROR.getMessage());
                }
                userInRedis.setQrCode(result);
            }catch (IOException e){
                throw new CustomRuntimeException(ResponseEnum.FETCH_USER_AVATAR_ERROR.getCode(),ResponseEnum.FETCH_USER_AVATAR_ERROR.getMessage());
            } catch (WriterException e) {
                throw new CustomRuntimeException(ResponseEnum.QRCODE_GENERATE_ERROR.getCode(),ResponseEnum.CAPTCHA_GENERATE_ERROR.getMessage());
            }
        }
        if (userInfoDTO.getNickname() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getNickname(), userInRedis.getNickname())) {
            userInRedis.setNickname(userInfoDTO.getNickname());
        }
        if (userInfoDTO.getStatus() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getStatus(), userInRedis.getStatus())) {
            userInRedis.setStatus(userInfoDTO.getStatus());
        }
        if (userInfoDTO.getLocation() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getLocation(), userInRedis.getLocation())) {
            userInRedis.setLocation(userInfoDTO.getLocation());
        }
        if (userInfoDTO.getJoinType() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getJoinType(), userInRedis.getJoinType())) {
            userInRedis.setJoinType(userInfoDTO.getJoinType());
        }
        if (userInfoDTO.getGender() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getGender(), userInRedis.getGender())) {
            userInRedis.setGender(userInfoDTO.getGender());
        }
        if (userInfoDTO.getSignature() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getSignature(), userInRedis.getSignature())) {
            userInRedis.setSignature(userInfoDTO.getSignature());
        }
        if (userInfoDTO.getUserType() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getUserType(), userInRedis.getUserType())) {
            userInRedis.setUserType(userInfoDTO.getUserType());
        }
        if (userInfoDTO.getAddType() != null && !ObjectUtils.nullSafeEquals(userInfoDTO.getAddType(), userInRedis.getAddType())) {
            userInRedis.setAddType(userInfoDTO.getAddType());
        }
        userMapper.updateByUserId(userInRedis.getUserId(),userInRedis);
        // 将更新后的 userInRedis 存储回 Redis
        RedisUtils.storeBeanAsJson(RedisKeys.USER_INFO_ID + user.getUserId(), userInRedis, SystemConstants.REDIS_ONE_WEEK_EXPIRATION);
    }

    @Override
    public String getQRCode() {
        User user = SecurityContextUtils.getUserNotNull();
        String userJson = RedisUtils.get(RedisKeys.USER_INFO_ID + user.getUserId());
        User userInRedis = JsonUtils.jsonStrToJavaObj(userJson, User.class);
        return userInRedis.getQrCode();
    }

    @Override
    public Date getUserUpdateTime() {
        User user = SecurityContextUtils.getUserNotNull();
        String userStr = RedisUtils.get(RedisKeys.USER_INFO_ID+user.getUserId());
        User userInRedis = JsonUtils.jsonStrToJavaObj(userStr,User.class);
        return userInRedis.getUpdateTime();
    }

    private void copyNonNullProperties(Object source, Object target) {
        BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);

        java.beans.PropertyDescriptor[] pds = srcWrapper.getPropertyDescriptors();

        for (java.beans.PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();
            Object srcValue = srcWrapper.getPropertyValue(propertyName);

            // 如果src中字段值不为空,使用src中的字段值
            if (srcValue != null && !(srcValue instanceof String && ((String) srcValue).trim().isEmpty())) {
                log.info("更新的字段为"+propertyName+"new value:"+srcValue);
                targetWrapper.setPropertyValue(propertyName, srcValue);
            }
        }
    }

    private byte[] downloadAvatar(String avatarUrl) throws IOException {
        // 从URL下载头像
        URL url = new URL(avatarUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
    @Override
    public TokenVO login(LoginDTO loginDTO) {
        String key;
        if(loginDTO instanceof PhoneLoginDTO){
            log.info("通过phone登录");
            key = ((PhoneLoginDTO) loginDTO).getPhone();
        }else if(loginDTO instanceof EmailLoginDTO){
            log.info("通过email登录");
            key = ((EmailLoginDTO) loginDTO).getEmail();
        }else{
            throw new CustomRuntimeException(ResponseEnum.NO_SUCH_LOGIN_METHOD.getCode(),ResponseEnum.NO_SUCH_LOGIN_METHOD.getMessage());
        }
        //校验用户密码是否正确,这里使用SpringSecurity的AuthManager来进行校验
        Authentication authRequest = UsernamePasswordAuthenticationToken.unauthenticated(key,loginDTO.getPassword());
        Authentication authResponse = customAuthManager.authenticate(authRequest);
        //校验成功后，将用户信息放到上下文对象中
        SecurityContextHolder.getContext().setAuthentication(authResponse);
        User user = SecurityContextUtils.getUserNotNull();
        user.setTerminal(loginDTO.getPlatform());
        log.info(user.getTerminal().toString());
        String userUid = user.getUserId();
        if(loginDTO instanceof PhoneLoginDTO){
            RedisUtils.set(RedisKeys.USER_LOGIN_PHONE+((PhoneLoginDTO) loginDTO).getPhone(),userUid);
        }else {
            RedisUtils.set(RedisKeys.USER_LOGIN_EMAIL+((EmailLoginDTO) loginDTO).getEmail(),userUid);
        }

        //查好友信息，存放到redis

        String privateChatKey = RedisKeys.USER_FRIENDS + userUid;
        if(!RedisUtils.hasKey(privateChatKey)){
            Set<String> friendUids = friendMapper.getFriendUidSet(userUid);
            RedisUtils.storeSet(privateChatKey,friendUids,SystemConstants.FRIEND_LIST_EXPIRATION);
        }
        //查所在的群聊信息，存放到redis
        String groupChatKey = RedisKeys.USER_GROUPS + userUid;
        if(!RedisUtils.hasKey(groupChatKey)){
            Set<String> groups = groupMapper.getMyGroups(userUid);
            RedisUtils.storeSet(groupChatKey,groups,SystemConstants.FRIEND_LIST_EXPIRATION);
        }

        //发放新的JWT令牌,AccessToken和RefreshToken AccessToken过期时间30min，RefreshToken过期时间设置成3天
        return generateNewTokenAndStore(user);
    }

    @Override
    public void logout() {
        User user = SecurityContextUtils.getUserNotNull();
        //删除Redis中的AccessToken和RefreshToken
        log.info(user.getAccessToken());
        String userId = user.getUserId();
        String platform = user.getTerminal().toString();
        RedisUtils.del(String.join(":",RedisKeys.USER_ACCESS_TOKEN,platform,userId));
        RedisUtils.del(String.join(":",RedisKeys.USER_REFRESH_TOKEN,platform,userId));
        //清除SpringContextHolder中的上下文对象
        SecurityContextHolder.clearContext();
    }

    @Override
    public TokenVO refreshToken(String refreshToken) {
        User user;
        try {
            String userId = JwtUtils.getUserIdFromToken(refreshToken);
            String userStrInRedis = RedisUtils.get(RedisKeys.USER_INFO_ID +userId);
            user = JsonUtils.jsonStrToJavaObj(userStrInRedis,User.class);
        }catch (Exception e){
            throw new CustomRuntimeException(ResponseEnum.USER_AUTH_ERROR.getCode(),ResponseEnum.USER_AUTH_ERROR.getMessage());
        }
        if(null==user){
            log.info("这里出现了问题，user为null");
            throw new CustomRuntimeException(ResponseEnum.USER_AUTH_ERROR.getCode(),ResponseEnum.USER_AUTH_ERROR.getMessage());
        }
        // Redis存储的RefreshToken与用户提供的RefreshToken不同,说明身份不同
        if(!refreshToken.equals(user.getRefreshToken())){
            log.info("这里出现了问题,refreshToken与Redis中保存的refreshToken不同");
            log.info(refreshToken+","+user.getRefreshToken());
            throw new CustomRuntimeException(ResponseEnum.USER_AUTH_ERROR.getCode(),ResponseEnum.USER_AUTH_ERROR.getMessage()+",token已过期");
        }
        //身份正确
        //生成新的accessToken和refreshToken 并保存

        return generateNewTokenAndStore(user);
    }

    @Override
    public UserInfoVO getUserInfo() {
        User user = SecurityContextUtils.getUserNotNull();
        String userStr = RedisUtils.get(RedisKeys.USER_INFO_ID+user.getUserId());
        User userInRedis = JsonUtils.jsonStrToJavaObj(userStr,User.class);
        log.info("AccessToken"+ user.getAccessToken());
        log.info("RefreshToken" + user.getRefreshToken());
        log.info(user.getStatus().toString());
        return BeanUtil.copyProperties(userInRedis, UserInfoVO.class);
    }

    private TokenVO generateNewTokenAndStore(User user){
        String userUid = user.getUserId();
        String userType = user.getUserType().toString();
        String platform = String.valueOf(user.getTerminal());
        String newAccessToken = JwtUtils.generateToken(userUid,userType,platform,SystemConstants.ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = JwtUtils.generateToken(userUid,userType,platform,SystemConstants.REFRESH_TOKEN_EXPIRATION);
        user.setRefreshToken(newRefreshToken);//设置新的refreshToken,保存到Redis中的用户信息中
        //保存到Redis中
        RedisUtils.storeBeanAsJson(RedisKeys.USER_INFO_ID +userUid,user,SystemConstants.REDIS_ONE_WEEK_EXPIRATION);
        RedisUtils.set(
                String.join(":",RedisKeys.USER_ACCESS_TOKEN,platform,userUid),
                newAccessToken,
                SystemConstants.REDIS_ACCESS_TOKEN_EXPIRATION);
        RedisUtils.set(String.join(":",RedisKeys.USER_REFRESH_TOKEN,platform,userUid),
                newRefreshToken,
                SystemConstants.REDIS_REFRESH_TOKEN_EXPIRATION);
        TokenVO tokenVO = new TokenVO();
        tokenVO.setUserId(userUid);
        tokenVO.setAccessToken(newAccessToken);
        tokenVO.setRefreshToken(newRefreshToken);
        return tokenVO;
    }
}
