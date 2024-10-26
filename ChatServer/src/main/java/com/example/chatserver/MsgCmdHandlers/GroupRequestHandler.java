package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.models.SendInfo;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class GroupRequestHandler implements CommandHandler{
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {

    }
}
