package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.SendResultEnum;
import com.example.chatcommon.enums.TerminalType;
import com.example.chatcommon.models.*;
import com.example.chatserver.UserChannelCtxMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class SendFileRequestHandler implements  CommandHandler{

    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            SendFileReqMsg sendFileReqMsg = objectMapper.readValue(jsonStr, SendFileReqMsg.class);
            String requestUserId = sendFileReqMsg.getSenderId();
            String friendUserId = sendFileReqMsg.getReceiverId();
            Set<Integer> terminals = TerminalType.codes();
            log.info("接收到好友请求，请求发送者:{},请求接收者:{}", requestUserId, friendUserId);
            terminals.forEach((terminal)->{
                ChannelHandlerContext receiverCtx = UserChannelCtxMap.getChannelCtx(friendUserId, terminal);
                //接受者在线,推送消息给他
                if(receiverCtx!=null){
                    SendInfo<Object> sendInfo = new SendInfo<>();
                    sendInfo.setCmd(NettyCmdType.SEND_FILE_REQUEST.getCode());
                    SendFileReqData data = new SendFileReqData();
                    data.setFileListHashCode(sendFileReqMsg.getFileListHashCode());
                    data.setSenderId(requestUserId);
                    data.setFileList(sendFileReqMsg.getFileList());
                    sendInfo.setData(data);
                    //log.info("receiver channel id: "+receiverCtx.channel().id().asShortText());
                    //log.info("sender channel id: "+ctx.channel().id().asShortText());
                    receiverCtx.channel().writeAndFlush(sendInfo);
                    log.info(requestUserId+"向"+friendUserId+"发送了文件");

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
