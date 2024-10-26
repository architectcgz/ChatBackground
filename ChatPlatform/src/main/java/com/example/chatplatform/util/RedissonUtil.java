package com.example.chatplatform.util;

import com.example.chatcommon.constants.ChatSystemConstants;
import com.example.chatcommon.models.SendInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @author archi
 */
@Slf4j
@Component
public class RedissonUtil {
    @Resource
    private RedissonClient redissonClient;
    public void sendMessage(SendInfo sendInfo){
        RTopic rTopic = redissonClient.getTopic(ChatSystemConstants.REDISSSON_MESSAGE_TOPIC);
        log.info("发送的信息: "+sendInfo.toString());
        rTopic.publish(sendInfo);
    }
}
