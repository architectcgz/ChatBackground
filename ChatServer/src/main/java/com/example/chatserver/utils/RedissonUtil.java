package com.example.chatserver.utils;

import com.example.chatcommon.constants.ChatSystemConstants;
import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.models.*;
import com.example.chatserver.MsgCmdHandlers.CommandHandlerFactory;
import com.example.chatserver.UserChannelCtxMap;
import com.example.chatserver.ws.endecode.MessageProtocolDecoder;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
/**
 * @author archi
 */
@Slf4j
@Component
public class RedissonUtil {
    @Resource
    private RedissonClient redissonClient;
    @PostConstruct
    public void listenMessage(){
        RTopic rTopic = redissonClient.getTopic(ChatSystemConstants.REDISSSON_MESSAGE_TOPIC);
        rTopic.addListener(SendInfo.class,(channel,sendInfo)->{
            log.info("收到广播信息: {}",sendInfo);
            NettyCmdType command = NettyCmdType.fromCode(sendInfo.getCmd());
            if(command==null){
                log.info("没有这一类型的消息");
                return;
            }
            switch (command){
                case PRIVATE_MESSAGE -> {
                    PrivateMessage message = (PrivateMessage) sendInfo.getData();
                    UserInfo sender = message.getSender();
                    ChannelHandlerContext userCtx = UserChannelCtxMap.getChannelCtx(sender.getUserId(),sender.getTerminal());
                    if(userCtx==null){
                        //通过登录接口进行登录,这里不登录
                        log.info("发送者未登录");
                        return;
                    }
                    CommandHandlerFactory.getHandler(command).handle(userCtx,sendInfo);
                }
                case GROUP_MESSAGE -> {
                    GroupMessage message = (GroupMessage) sendInfo.getData();
                    UserInfo sender = message.getSender();
                    ChannelHandlerContext userCtx = UserChannelCtxMap.getChannelCtx(sender.getUserId(),sender.getTerminal());
                    if(userCtx==null){
                        //通过登录接口进行登录,这里不登录
                        log.info("发送者未登录,测试时不需要登录，工作时需要登录，记得修改");
                        //CommandHandlerFactory.getHandler(command).handle(userCtx,sendInfo);
                        return;
                    }
                    CommandHandlerFactory.getHandler(command).handle(userCtx,sendInfo);
                }
                case FRIEND_REQUEST -> {
                    log.info("收到了好友请求信息");

                    FriendRequestMessage friendRequestMessage = (FriendRequestMessage) sendInfo.getData();
                    ChannelHandlerContext userCtx = UserChannelCtxMap.getChannelCtx(friendRequestMessage.getRequestUserId(), friendRequestMessage.getTerminal());
                    if(userCtx==null){
                        //通过登录接口进行登录,这里不登录
                        log.info("发送者未登录,测试时不需要登录，工作时需要登录，记得修改");
                        CommandHandlerFactory.getHandler(command).handle(userCtx,sendInfo);
                        return;
                    }
                    CommandHandlerFactory.getHandler(command).handle(userCtx,sendInfo);
                }
            }

        });
    }
    public void sendMessage(SendInfo<?> sendInfo){
        RTopic rTopic = redissonClient.getTopic(ChatSystemConstants.REDISSSON_MESSAGE_TOPIC);
        rTopic.publish(sendInfo);
    }


}
