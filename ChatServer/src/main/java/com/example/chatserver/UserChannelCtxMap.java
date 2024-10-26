package com.example.chatserver;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserChannelCtxMap {

    private static ConcurrentHashMap<String, Map<Integer, ChannelHandlerContext>>USER_CHANNEL_CTX_MAP = new ConcurrentHashMap<>();
    public static Integer getUserChannelCount(String userId){
        return USER_CHANNEL_CTX_MAP.get(userId).size();
    }
    public static void addContext(String userId, Integer terminal, ChannelHandlerContext ctx){
        USER_CHANNEL_CTX_MAP.computeIfAbsent(userId,key->new ConcurrentHashMap<>()).put(terminal,ctx);
    }
    public static Set<String> getKeys() {
        return USER_CHANNEL_CTX_MAP.keySet();
    }

    public static void removeContext(String userId, Integer terminal) {
        if (userId != null && terminal != null && USER_CHANNEL_CTX_MAP.containsKey(userId)) {
            Map<Integer, ChannelHandlerContext> userChannelMap = USER_CHANNEL_CTX_MAP.get(userId);
            if (userChannelMap != null) {
                userChannelMap.remove(terminal);
                log.info("移除客户端: {} userId: {}. 剩余客户端的数量: {}", terminal, userId, userChannelMap.size());
            }
        }
    }

    public static ChannelHandlerContext getChannelCtx(String userId,Integer terminal){
        if(userId!=null&&terminal!=null&& USER_CHANNEL_CTX_MAP.containsKey(userId)){
            Map<Integer,ChannelHandlerContext> userChannelMap = USER_CHANNEL_CTX_MAP.get(userId);
            if(userChannelMap.containsKey(terminal)){
                return userChannelMap.get(terminal);
            }
        }
        return null;
    }

    public static Map<Integer,ChannelHandlerContext>getChannelCtx(String userId){
        if(userId!=null&& USER_CHANNEL_CTX_MAP.containsKey(userId)){
            return USER_CHANNEL_CTX_MAP.get(userId);
        }
        return null;
    }
}
