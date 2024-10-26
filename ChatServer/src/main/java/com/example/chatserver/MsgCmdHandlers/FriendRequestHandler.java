package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.SendResultEnum;
import com.example.chatcommon.enums.TerminalType;
import com.example.chatcommon.models.FriendRequestMessage;
import com.example.chatcommon.models.RequestFriendInfo;
import com.example.chatcommon.models.SendInfo;
import com.example.chatserver.UserChannelCtxMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
@Slf4j
@Component
public class FriendRequestHandler implements CommandHandler{
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            FriendRequestMessage friendRequestMessage = objectMapper.readValue(jsonStr, FriendRequestMessage.class);
            String requestUserId = friendRequestMessage.getRequestUserId();
            String friendUserId = friendRequestMessage.getFriendUid();
            Set<Integer> terminals = TerminalType.codes();
            log.info("接收到好友请求，请求发送者:{},请求接收者:{}", requestUserId, friendUserId);
            terminals.forEach((terminal)->{
                ChannelHandlerContext receiverCtx = UserChannelCtxMap.getChannelCtx(friendUserId, terminal);
                //接受者在线,推送消息给他
                if(receiverCtx!=null){
                    SendInfo<Object> sendInfo = new SendInfo<>();
                    sendInfo.setCmd(NettyCmdType.FRIEND_REQUEST.getCode());
                    RequestFriendInfo requestFriendInfo = new RequestFriendInfo(
                            friendRequestMessage.getRequestUserId(),
                            friendRequestMessage.getAvatar(),
                            friendRequestMessage.getNickname(),
                            friendRequestMessage.getGender(),
                            friendRequestMessage.getRequestMessage(),
                            friendRequestMessage.getLocation(),
                            friendRequestMessage.getStatus(),
                            friendRequestMessage.getCreateTime(),
                            friendRequestMessage.getUpdateTime()
                    );
                    sendInfo.setData(requestFriendInfo);
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
