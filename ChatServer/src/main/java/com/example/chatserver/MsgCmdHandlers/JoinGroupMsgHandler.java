package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.models.GroupMessage;
import com.example.chatcommon.models.SendInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;

public class JoinGroupMsgHandler implements CommandHandler{
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg);
            GroupMessage groupMessage = objectMapper.readValue(jsonStr,GroupMessage.class);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
