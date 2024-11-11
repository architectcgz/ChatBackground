package com.example.chatcommon.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class JwtUtils {
    private static final SecretKey secret = Keys.hmacShaKeyFor(System.getenv("JWT_KEY").getBytes());

//    static {
//        String jwtKey = System.getenv("JWT_KEY");
//        if (jwtKey == null || jwtKey.isEmpty()) {
//            throw new IllegalArgumentException("JWT_KEY environment variable is not set or is empty.");
//        }
//        log.info("从环境变量中加载出jwtKey: {}", jwtKey);
//        secret = Keys.hmacShaKeyFor(jwtKey.getBytes());
//    }
    /**
     * 生成失效时间，以秒为单位
     *
     * @return 预计失效时间
     */
    private Object generateExpirationDate(Long millis) {
        //预计失效时间为：token生成时间+预设期间
        //RefreshToken这里设置成72小时过期 1000 * 60 * 60* 72, 1000ms = 1s
        return new Date(System.currentTimeMillis() + millis);
    }

    public static boolean isTokenValid(String token){
        return !isTokenExpired(token);
    }

    public static String generateToken(String userId,String userType,String platform,Long expiration){
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + expiration);
        return Jwts.builder()
                .signWith(secret,Jwts.SIG.HS256)
                .subject(userId)
                .claim("userType",userType)
                .claim("platform",platform)
                .issuedAt(now)
                .expiration(exp)
                .signWith(secret,Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 从token中拿到用户类型
     * @param token
     * @return
     */
    public static String getUserTypeFromToken(String token){
        Claims payload = getPayload(token);
        if(payload!=null){
            return payload.get("userType",String.class);
        }
        return null;
    }

    /**
     * 从token中获取用户所在平台
     * @param token
     * @return 用户所在平台
     */
    public static String getPlatformFromToken(String token){
        Claims payload = getPayload(token);
        if(payload!=null){
            return payload.get("platform",String.class);
        }
        log.info("不能从token的payload中获取到platform的信息");
        return null;
    }
    /**
     * 从token中获取用户id
     * @param token
     * @return 用户id
     */
    public static String getUserIdFromToken(String token){
        Claims payload = getPayload(token);
        if(payload!=null){
            return payload.getSubject();
        }
        return null;
    }

    /**
     * 从Token中获取负载中的Claims
     * @param token token
     * @return 负载
     */
    private static Claims getPayload(String token)
    {
        try {
            return Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (ExpiredJwtException e){
            log.info("token"+ token+"已过期");
            return null;
        }
    }

    /**
     * 判断token是否有过期
     * @param token 需要被验证的token
     * @return true/false
     */
    public static boolean isTokenExpired(String token)
    {
        //判断预设时间是否在当前时间之前，如果在当前时间之前，就表示过期了，会返回true
        Date expiredDate = getExpiredDate(token);
        if(expiredDate==null){
            return true;
        }
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取预设的过期时间
     * @param token token
     * @return 预设的过期时间
     */
    private static Date getExpiredDate(String token)
    {
        Claims payload = getPayload(token);
        if(payload!=null){
            return payload.getExpiration();
        }
        return null;
    }

    /**
     * 判断token是否可以被刷新
     * @param token 需要被验证的token
     * @return true/false
     */
    public boolean canRefresh(String token){
        return isTokenExpired(token);
    }

    public Map<String,String> generateTokenMap(String accessToken,String refreshToken){
        return new HashMap<>(){
            {
                put("accessToken",accessToken);
                put("refreshToken",refreshToken);
            }
        };
    }
}
