package com.example.chatclient;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    public Boolean isUserOnline(String userId) {

    }
}
