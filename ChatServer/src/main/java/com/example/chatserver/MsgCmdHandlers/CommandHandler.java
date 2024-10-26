package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.models.SendInfo;
import io.netty.channel.ChannelHandlerContext;

public interface CommandHandler {
    void handle(ChannelHandlerContext ctx, SendInfo msg);
}
