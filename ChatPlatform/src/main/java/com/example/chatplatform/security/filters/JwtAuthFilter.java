package com.example.chatplatform.security.filters;

import com.example.chatcommon.utils.JwtUtils;
import com.example.chatplatform.entity.CustomException;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.security.CustomUserDetails;
import com.example.chatplatform.security.CustomUserDetailsService;
import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.util.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * @author archi
 */
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailService;
    public JwtAuthFilter(CustomUserDetailsService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("进入TokenAuthFilter");
        log.info("请求url为: "+ request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        //token为空,允许通过让其登录获取token或访问其他不拦截的url
        if(ObjectUtils.isEmpty(authHeader) ||!authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String accessJwt = authHeader.substring(7);
        String username = JwtUtils.getUserIdFromToken(accessJwt);;
        Integer platform = Integer.valueOf(JwtUtils.getPlatformFromToken(accessJwt));
        //用户不为空且SecurityContextHolder中没有authentication对象的时候
        if(!ObjectUtils.isEmpty(username) &&
                SecurityContextHolder.getContext().getAuthentication()==null){
            String redisAccessToken = RedisUtils.get(String.join(":",RedisKeys.USER_ACCESS_TOKEN,platform.toString(),username));
            boolean accessTokenValid = false;
            if(redisAccessToken!=null&&redisAccessToken.equals(accessJwt)){
                accessTokenValid = true;
            }
            //如果redis中存有accessToken,那么accessToken未过期,可以访问
            //如果accessToken没有过期，但是redis中没有，说明其他处的登录挤掉了本次登录
            log.info("Redis中有AccessToken信息: "+accessTokenValid);
            if(JwtUtils.validateToken(accessJwt)&&accessTokenValid){
                log.info("token未过期且Redis中有AccessToken信息");
                CustomUserDetails userDetails = (CustomUserDetails) userDetailService.loadUserByUsername(username);
                //获取用户使用的平台
                User user = userDetails.getUser();
                user.setTerminal(platform);
                userDetails.setUser(user);
                //如果令牌有效,封装一个UsernamePasswordAuthenticationToken对象
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //更新安全上下文的持有用户
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }
        filterChain.doFilter(request,response);
    }
}
