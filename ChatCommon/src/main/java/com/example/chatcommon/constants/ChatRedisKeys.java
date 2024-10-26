package com.example.chatcommon.constants;

public class ChatRedisKeys {
    public static final String CHAT_MAX_SERVER_ID = "chat:max_server_id:";
    /*
        未读消息联系人集合
     */
    public static final String CHAT_UNREAD_CONTACT_SET = "chat:message:unread:contact_set:";
    public static final String CHAT_USER_SERVER_ID = "chat:user:server_id:";
    /**
     * 未读消息的格式是 chat:message:private:unread:terminal:receiverId:senderId
     */
    public static final String CHAT_PRIVATE_MESSAGE_UNREAD = "chat:message:private:unread:";
    public static final String CHAT_PRIVATE_MESSAGE_READ = "chat:message:private:read:";
    /**
     * 未读消息的格式是 chat:message:group:unread:terminal:groupId:memberId
     */
    public static final String CHAT_GROUP_MESSAGE_UNREAD = "chat:message:group:unread:";
    public static final String CHAT_GROUP_MESSAGE_READ = "chat:message:group:read:";
    public static final String CHAT_USER_ACCESS_TOKEN = "chat:user:token:access:";


    public static final String USER_ACCESS_TOKEN = "chat:user:token:access";
    public static final String USER_REFRESH_TOKEN = "chat:user:token:refresh";
}
