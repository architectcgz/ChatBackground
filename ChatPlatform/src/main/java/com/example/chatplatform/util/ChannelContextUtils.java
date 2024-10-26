package com.example.chatplatform.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author archi
 */
public class ChannelContextUtils {
    public static void addContext(String userId, Channel channel){
        String channelId = channel.id().toString();
        AttributeKey attributeKey;
        if(!AttributeKey.exists(channelId)){
            attributeKey = AttributeKey.newInstance(channelId);
        }else{
            attributeKey = AttributeKey.valueOf(channelId);
        }
        channel.attr(attributeKey).set(userId);
    }
}
