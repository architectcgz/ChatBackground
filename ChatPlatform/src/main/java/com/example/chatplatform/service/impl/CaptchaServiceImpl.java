package com.example.chatplatform.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.example.chatplatform.entity.CustomException;
import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.service.CaptchaService;
import com.example.chatplatform.util.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author archi
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Resource
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    public void emailCaptcha(String receiverEmail) {
        String captchaCode = RandomUtil.randomNumbers(6);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //发送者邮箱
        simpleMailMessage.setFrom(emailFrom);
        //接收者邮箱
        simpleMailMessage.setTo(receiverEmail);
        //主题标题
        simpleMailMessage.setSubject("你在Chat上的注册验证码");
        //信息内容
        simpleMailMessage.setText(captchaCode);
        try {
            javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            throw new CustomException(ResponseEnum.CAPTCHA_SEND_ERROR.getCode(),ResponseEnum.CAPTCHA_SEND_ERROR.getMessage());
        }
        //验证码保存到redis
        RedisUtils.set(RedisKeys.EMAIL_CAPTCHA+receiverEmail,captchaCode,SystemConstants.REDIS_CAPTCHA_EXPIRATION);
    }

    @Override
    public void phoneCaptcha(String phone) {
        //可以使用阿里的sms短信服务等
        throw new CustomException(ResponseEnum.RESOURCE_NOT_FOUND_ERROR.getCode(),ResponseEnum.RESOURCE_NOT_FOUND_ERROR.getMessage());
    }

}
