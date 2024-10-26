package com.example.chatplatform.service;

/**
 * 发送验证码Service
 */
public interface CaptchaService {


    void emailCaptcha(String receiverEmail);
    void phoneCaptcha(String phone);
}
