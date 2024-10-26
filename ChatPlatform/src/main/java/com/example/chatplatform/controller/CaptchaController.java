package com.example.chatplatform.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.entity.response.ResponseEntity;
import com.example.chatplatform.service.CaptchaService;
import com.example.chatplatform.util.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author archi
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Resource
    private CaptchaService captchaService;
    /**
     * web 端可以使用的图片验证码
     * @param cookieHeader 每个客户的特殊标识符,用于区别用户
     * @param response image/png类型 图片验证码
     * @return
     */
    @GetMapping("/web")
    public ResponseEntity captchaWeb(@RequestHeader(value = "Cookie",required = false)String cookieHeader, HttpServletResponse response){
        LineCaptcha lineCaptcha =  CaptchaUtil.createLineCaptcha(140,40);
        String sessionId = null;
        try {
            response.setContentType("image/png");
            if(null!=cookieHeader){
                for (String cookie : cookieHeader.split(";")) {
                    cookie = cookie.trim();
                    if (cookie.startsWith("SESSIONID=")) {
                        sessionId = cookie.substring("SESSIONID=".length());
                        break;
                    }
                }
            }

            //如果没有提供sessionId,则生成一个新的
            if(null==sessionId||sessionId.isEmpty()){
                sessionId = UUID.randomUUID().toString();
                Cookie sessionCookie = new Cookie("SESSIONID", sessionId);
                sessionCookie.setPath("/");
                sessionCookie.setHttpOnly(true);
                //不需要设置cookie过期时间，这里设置成对话cookie
                // 设置Cookie过期时间 30min
                //sessionCookie.setMaxAge(SystemConstants.CAPTCHA_EXPIRATION.intValue()*60);
                response.addCookie(sessionCookie);
            }
            //删除上次的验证码
            String oldCaptchaKey = RedisKeys.CAPTCHA+sessionId;
            if(RedisUtils.hasKey(oldCaptchaKey)){
                RedisUtils.del(oldCaptchaKey);
            }

            lineCaptcha.write(response.getOutputStream());
            String code = lineCaptcha.getCode();

            //将新的验证码保存到redis
            RedisUtils.set(RedisKeys.CAPTCHA +sessionId,code, SystemConstants.REDIS_CAPTCHA_EXPIRATION);
            log.info(code);
            response.getOutputStream().close();
            //sessionId返回客户端
            return new ResponseEntity<>().ok();
        }catch (Exception e){
            return new ResponseEntity<>().error(ResponseEnum.CAPTCHA_GENERATE_ERROR.getCode(), ResponseEnum.CAPTCHA_GENERATE_ERROR.getMessage());
        }
    }

    /**
     * 邮件验证码
     * @param email 接收者的邮箱
     * @return
     */
    @GetMapping("/email")
    public ResponseEntity emailCaptcha(@RequestParam("email")String email){
        captchaService.emailCaptcha(email);
        return new ResponseEntity<>().ok();
    }
    @GetMapping("/phone")
    public ResponseEntity smsCaptcha(@RequestParam("phone")String phone){
        captchaService.phoneCaptcha(phone);
        return new ResponseEntity<>().ok();
    }
}
