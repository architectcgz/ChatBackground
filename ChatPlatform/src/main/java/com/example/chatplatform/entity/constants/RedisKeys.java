package com.example.chatplatform.entity.constants;

public class RedisKeys {
    public static final String CAPTCHA = "chat:captcha:";
    public static final String EMAIL_CAPTCHA = "chat:captcha:email:";
    public static final String PHONE_CAPTCHA = "chat:captcha:phone:";
    public static final String USER_INFO_ID = "chat:user:info:";
    public static final String USER_LOGIN_PHONE = "chat:user:login:phone:";
    public static final String USER_LOGIN_EMAIL = "chat:user:login:email:";

    public static final String USER_REFRESH_TOKEN = "chat:user:token:refresh";
    public static final String USER_ACCESS_TOKEN = "chat:user:token:access";
    public static final String USER_FRIENDS = "chat:friend:";
    public static final String USER_GROUPS = "chat:group:";
    public static final String NETTY_CHANNEL = "chat:channel:";
    public static final String NETTY_HEART_BEAT = "chat:heat_beat:";

    public static final String MAX_SERVER_ID = "chat:max_server_id";
    public static final String USER_SERVER_ID = "chat:user:server_id";
    public static final String MSG_RESULT_PRIVATE_QUEUE = "chat:message:result:private";

    public static final String MSG_RESULT_GROUP_QUEUE = "chat:message:result:group";
}
