package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.SendResultEnum;
import com.example.chatcommon.enums.TerminalType;
import com.example.chatcommon.models.AcceptSendFileReq;
import com.example.chatcommon.models.FriendRequestMessage;
import com.example.chatcommon.models.RequestFriendInfo;
import com.example.chatcommon.models.SendInfo;
import com.example.chatserver.UserChannelCtxMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class AcceptFileRequestHandler implements CommandHandler{
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            AcceptSendFileReq acceptSendFileReq = objectMapper.readValue(jsonStr, AcceptSendFileReq.class);
            String requestUserId = acceptSendFileReq.getAcceptUserId();
            String friendUserId = acceptSendFileReq.getSendUserId();
            Set<Integer> terminals = TerminalType.codes();
            log.info("同意好友发送文件的请求，请求发送者:{},请求接收者:{}", requestUserId, friendUserId);
            terminals.forEach((terminal)->{
                ChannelHandlerContext receiverCtx = UserChannelCtxMap.getChannelCtx(friendUserId, terminal);
                //接受者在线,推送消息给他
                if(receiverCtx!=null){
                    SendInfo<Object> sendInfo = new SendInfo<>();
                    sendInfo.setCmd(NettyCmdType.ACCEPT_FILE_REQUEST.getCode());
                    sendInfo.setData(acceptSendFileReq);
                    //log.info("receiver channel id: "+receiverCtx.channel().id().asShortText());
                    //log.info("sender channel id: "+ctx.channel().id().asShortText());
                    receiverCtx.channel().writeAndFlush(sendInfo);

                }else{
                    log.info("未找到用户所在ChannelHandlerContext, 可能是用户不在线 请求发送者:"+requestUserId
                            +", 请求接收者: "+friendUserId);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            SendInfo<Object> sendInfo = new SendInfo<>();
            sendInfo.setCmd(NettyCmdType.PRIVATE_MESSAGE.getCode());
            sendInfo.setData(SendResultEnum.FAIL.getCode());
            ctx.channel().writeAndFlush(sendInfo);
        }
    }
}
