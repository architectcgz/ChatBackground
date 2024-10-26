package com.example.chatplatform.entity.constants;

/**
 * @author archi
 */
public class SystemConstants {
    public static final Integer MAX_FILENAME_LENGTH = 100;
    public static final String NORMAL_USER = "NORMAL_USER";
    public static final String ROLE_NORMAL_USER = "ROLE_NORMAL_USER";
    public static final String VIP_USER = "VIP_USER";
    public static final String ROLE_VIP_USER = "ROLE_VIP_USER";
    //3 days
    public static final Long REFRESH_TOKEN_EXPIRATION = 3*24*60*60*1000L;

    //30min过期  1800000L
    //1min 过期 60000L
    //TODO
    //这里记得修改回来 30min过期
    public static final Long ACCESS_TOKEN_EXPIRATION = 60000000L;

    //30min过期 30L
    //测试刷新token，accessToken有效期设置成1min
    //TODO
    //这里记得修改回来 30min过期
    public static final Long REDIS_ACCESS_TOKEN_EXPIRATION = 1000L;
    //3天过期
    public static final Long REDIS_REFRESH_TOKEN_EXPIRATION = 4320L;


    public static final Long REDIS_ONE_DAY_EXPIRATION = 1440L;
    public static final Long REDIS_THREE_DAYS_EXPIRATION = 4320L;

    public static final Long REDIS_ONE_WEEK_EXPIRATION = 10080L;



    //验证码5min过期
    public static final Long REDIS_CAPTCHA_EXPIRATION = 5L;
    //好友列表一周过期
    public static final Long FRIEND_LIST_EXPIRATION = 10080L;
    //群组列表一周过期
    public static final Long GROUP_LIST_EXPIRATION = 10800L;

    public static final Long HEART_BEAT = 60L;

    public static final Long READ_TIME_OUT = 180L;
    public static final Long NETTY_ONLINE_TIMEOUT = 600L;
    public static final Long ONLINE_TIMEOUT_SECOND = 600L;
    public static final Long SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L;
}
