package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.SendResultEnum;
import com.example.chatcommon.enums.TerminalType;
import com.example.chatcommon.models.FileSliceData;
import com.example.chatcommon.models.FileSliceMessage;
import com.example.chatcommon.models.SendInfo;
import com.example.chatserver.UserChannelCtxMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
@Slf4j
@Component
public class FileHandler implements CommandHandler{
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            FileSliceMessage fileSliceMessage = objectMapper.readValue(jsonStr, FileSliceMessage.class);
            String senderId = fileSliceMessage.getSenderId();
            String receiverId = fileSliceMessage.getReceiverId();
            Set<Integer> terminals = TerminalType.codes();
            log.info("接收到好友请求，请求发送者:{},请求接收者:{}", senderId, receiverId);
            terminals.forEach((terminal)->{
                ChannelHandlerContext receiverCtx = UserChannelCtxMap.getChannelCtx(receiverId, terminal);
                //接受者在线,推送消息给他
                if(receiverCtx!=null){
                    SendInfo<Object> sendInfo = new SendInfo<>();
                    sendInfo.setCmd(NettyCmdType.FILE_SLICE.getCode());
                    FileSliceData data = new FileSliceData();
                    data.setFileName(fileSliceMessage.getFileName());
                    data.setSenderId(fileSliceMessage.getSenderId());
                    data.setData(fileSliceMessage.getData());
                    data.setChunkIndex(fileSliceMessage.getChunkIndex());
                    data.setTotalChunks(fileSliceMessage.getTotalChunks());
                    sendInfo.setData(data);
                    log.info("receiver channel id: "+receiverCtx.channel().id().asShortText());
                    log.info("sender channel id: "+ctx.channel().id().asShortText());
                    receiverCtx.channel().writeAndFlush(sendInfo);

                }else{
                    log.info("未找到用户所在ChannelHandlerContext, 可能是用户不在线 请求发送者:"+senderId
                            +", 请求接收者: "+receiverId);
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
