package com.example.chatclient;

import com.example.chatcommon.models.GroupMessage;
import com.example.chatcommon.models.PrivateMessage;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.util.Set;

@Configuration
@AllArgsConstructor
public class ChatClient {
    @Resource
    private final WebSocketService webSocketService;

    /**
     * 判断用户是否在线
     * @param userId
     * @return
     */
    public Boolean isUserOnline(String userId){
        return webSocketService.isUserOnline(userId);
    }

    /**
     * 获取在线用户集合
     * @param userId
     * @return
     */
    public Set<String> getOnlineUsers(Set<String>userId){
        return webSocketService.getOnlineUsers(userId);
    }

    public<T> void sendPrivateMessage(PrivateMessage<T> message){
        return webSocketService.sendPrivateMessage(message);
    }

    public<T> void sendGroupMessage(GroupMessage<T>message){
        return webSocketService.sendGroupMessage(message);
    }

}
