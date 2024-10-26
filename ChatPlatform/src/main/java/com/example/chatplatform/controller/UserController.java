package com.example.chatplatform.controller;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.models.GroupMessage;
import com.example.chatcommon.models.SendInfo;
import com.example.chatcommon.models.UserInfo;
import com.example.chatplatform.entity.dto.UserInfoDTO;
import com.example.chatplatform.entity.dto.login.EmailLoginDTO;
import com.example.chatplatform.entity.dto.login.PhoneLoginDTO;
import com.example.chatplatform.entity.dto.register.EmailRegisterDTO;
import com.example.chatplatform.entity.dto.register.PhoneRegisterDTO;
import com.example.chatplatform.entity.response.ResponseEntity;
import com.example.chatplatform.service.UserService;
import com.example.chatplatform.util.RedissonUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;


/**
 * @author archi
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedissonUtil redissonUtil;
    @GetMapping("/test")
    public ResponseEntity test(){
        GroupMessage<Object> groupMessage = new GroupMessage<>();
        groupMessage.setData("ceshi");
        groupMessage.setReceiverIdSet(new HashSet<>());
        groupMessage.getReceiverIdSet().add("123123");

        groupMessage.setReceiveTerminals(new HashSet<>());
        groupMessage.getReceiveTerminals().add(1);

        groupMessage.setSender(new UserInfo("00000", 1));
        groupMessage.setSendResult(false);

        SendInfo<GroupMessage<Object>> sendInfo = new SendInfo<>();
        sendInfo.setCmd(NettyCmdType.GROUP_MESSAGE.getCode());
        sendInfo.setData(groupMessage);

        redissonUtil.sendMessage(sendInfo);
        return new ResponseEntity<>().ok();
    }

    @GetMapping("/qrcode")
    public ResponseEntity getQRCode(){
        return new ResponseEntity<>(userService.getQRCode()).ok();
    }
    /**
     * TODO
     * 其他端注册方案,发送手机验证码,暂时用email验证码代替
     * @param registerDTO
     * @return
     */
    @PostMapping("/register/phone")
    public ResponseEntity appRegister(@RequestBody @Valid PhoneRegisterDTO registerDTO){
        userService.register(registerDTO);
        return new ResponseEntity<>().ok();
    }

    @PostMapping("/register/email")
    public ResponseEntity registerByEmail(@RequestBody @Valid EmailRegisterDTO registerDTO){
        userService.register(registerDTO);
        return new ResponseEntity<>().ok();
    }

    @PostMapping("/login/email")
    public ResponseEntity loginByEmail(@RequestBody @Valid EmailLoginDTO loginDTO){
        return new ResponseEntity<>(userService.login(loginDTO)).ok();
    }

    @PostMapping("/login/phone")
    public ResponseEntity loginByPhone(@RequestBody @Valid PhoneLoginDTO loginDTO){
        return new ResponseEntity<>(userService.login(loginDTO)).ok();
    }
    @PostMapping("/logout")
    public ResponseEntity logout(){
        userService.logout();
        return new ResponseEntity<>().ok();
    }

    @PostMapping("/refresh_token")
    public ResponseEntity refreshToken(@RequestHeader(value = "RefreshToken",required = false)String refreshToken){
        //这里required = false 是为了在refreshToken中处理异常情况
        return new ResponseEntity<>(userService.refreshToken(refreshToken)).ok();
    }
    @GetMapping("/info")
    public ResponseEntity userInfo(){
        return new ResponseEntity<>(userService.getUserInfo()).ok();
    }
    @GetMapping("/update_time")
    public ResponseEntity getUpdateTime(){
        return new ResponseEntity(userService.getUserUpdateTime()).ok();
    }
    @PostMapping("/info/update")
    public ResponseEntity modifyUserInfo(@RequestBody UserInfoDTO userInfoDTO){
        userService.updateUserInfo(userInfoDTO);
        return new ResponseEntity<>().ok();
    }
}
