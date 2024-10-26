package com.example.chatplatform.security.config;

import com.example.chatplatform.security.CustomAuthManager;
import com.example.chatplatform.security.CustomAuthProvider;
import com.example.chatplatform.security.CustomUserDetailsService;
import com.example.chatplatform.security.filters.JwtAuthFilter;
import com.example.chatplatform.security.handlers.CustomAccessDeniedHandler;
import com.example.chatplatform.security.handlers.InvalidAuthEntryPoint;
import com.example.chatplatform.entity.constants.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author archi
 */
@Configuration
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SpringSecurityConfig {
    private final InvalidAuthEntryPoint invalidAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final String []WHITE_LIST={
            "/user/register/*",
            "/user/login/*",
            "/user/refresh_token",
            "/captcha/*"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 禁用basic明文验证
                .httpBasic(httpBasic-> httpBasic.disable())
                // 前后端分离架构不需要csrf保护
                .csrf(csrf-> csrf.disable())
                // 禁用默认登录页
                .formLogin(formLogin-> formLogin.disable())
                // 禁用默认登出页
                .logout(logout-> logout.disable())
                // 前后端分离是无状态的，不需要session了，直接禁用。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        // 允许所有OPTIONS请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 允许直接访问授权登录接口 授权检查中放行这些请求，而不是绕过过滤器链中的过滤器。
                        .requestMatchers(WHITE_LIST).permitAll()
                        // 其他所有接口必须有Authority信息，Authority在登录成功后的UserDetailsImpl对象中默认设置“ROLE_USER”
                        .requestMatchers("/user/*").hasAnyRole(SystemConstants.NORMAL_USER,SystemConstants.VIP_USER)
                        // 允许任意请求被已登录用户访问，不检查Authority
                        .anyRequest().authenticated()
                )
                .authenticationProvider(new CustomAuthProvider(passwordEncoder(), customUserDetailsService()))
                .addFilterBefore(new JwtAuthFilter(customUserDetailsService()),
                        UsernamePasswordAuthenticationFilter.class
                )
                 //设置异常的EntryPoint，如果不设置，默认使用Http403ForbiddenEntryPoint
                .exceptionHandling(
                        exceptions->exceptions.authenticationEntryPoint(invalidAuthEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .build();
    }
    @Bean
    public CustomUserDetailsService customUserDetailsService(){
        return new CustomUserDetailsService();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CustomAuthManager customAuthManager(){
        return new CustomAuthManager(passwordEncoder(), customUserDetailsService());
    }


}
