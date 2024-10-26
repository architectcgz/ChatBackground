package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.models.SendInfo;
import com.example.chatserver.constants.ChannelAttrKeys;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeartBeatHandler implements CommandHandler{
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        SendInfo sendInfo = new SendInfo();
        sendInfo.setCmd(NettyCmdType.HEART_BEAT.getCode());
        ctx.channel().writeAndFlush(sendInfo);
        log.info("收到客户端"+ctx.channel().id()+"的心跳消息");
    }
}
