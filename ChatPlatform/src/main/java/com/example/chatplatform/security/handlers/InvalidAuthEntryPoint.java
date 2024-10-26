package com.example.chatplatform.security.handlers;

import com.example.chatplatform.entity.enums.ResponseEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author archi
 */
@Component
public class InvalidAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 设置响应状态码为 401 未认证
        response.sendError(ResponseEnum.USER_AUTH_ERROR.getCode(), ResponseEnum.USER_AUTH_ERROR.getMessage());
    }
}
