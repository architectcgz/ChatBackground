package com.example.chatserver.handlers;

import com.example.chatserver.UserChannelCtxMap;
import com.example.chatserver.constants.ChannelAttrKeys;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author archi
 */
@Slf4j
public class HeartBeatHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            Channel channel = ctx.channel();
            Attribute<String> attribute =  channel.attr(AttributeKey.valueOf(ChannelAttrKeys.USER_ID));
            String userId = attribute.get();
            if(e.state().equals(IdleState.READER_IDLE)){
                log.info(userId+"进入读空闲");
                //此时关闭这个链接  关闭channelHandlerCtx是异步的,所以不要在这里查看关闭后的channel count
                ctx.close();
            }
        }
    }
}
