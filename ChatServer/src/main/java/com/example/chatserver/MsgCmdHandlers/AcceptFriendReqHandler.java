package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.TerminalType;
import com.example.chatcommon.models.AcceptFriendRequest;
import com.example.chatcommon.models.SendInfo;
import com.example.chatserver.UserChannelCtxMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class AcceptFriendReqHandler implements CommandHandler{
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            AcceptFriendRequest acceptFriendRequest = objectMapper.readValue(jsonStr, AcceptFriendRequest.class);
            String notifyUserId = acceptFriendRequest.getRequestUserId();

            Set<Integer> terminals = TerminalType.codes();
            Set<String> keys = UserChannelCtxMap.getKeys();
            log.info("连接的客户端数量: "+keys.size());
            keys.forEach(
                    key->{log.info(key);}
            );
            terminals.forEach(terminal->{
                ChannelHandlerContext receiverCtx = UserChannelCtxMap.getChannelCtx(notifyUserId,terminal);

                if(receiverCtx!=null){
                    log.info("要通知的用户为: "+notifyUserId+"平台: "+terminal +"在线");
                    SendInfo sendInfo = new SendInfo();
                    sendInfo.setCmd(NettyCmdType.ACCEPT_FRIEND_REQUEST.getCode());
                    sendInfo.setData(acceptFriendRequest);
                    receiverCtx.channel().writeAndFlush(sendInfo);
                }else{
                    log.info("要通知的用户为: "+notifyUserId+"平台: "+terminal +"不在线");
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
