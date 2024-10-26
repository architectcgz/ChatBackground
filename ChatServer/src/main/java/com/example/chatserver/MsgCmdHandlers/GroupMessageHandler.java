package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.constants.ChatRedisKeys;
import com.example.chatcommon.enums.MessageStatusEnum;
import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.SendResultEnum;
import com.example.chatcommon.models.*;
import com.example.chatserver.UserChannelCtxMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author archi
 */
@Slf4j
@Component
public class GroupMessageHandler implements CommandHandler{
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            GroupMessage groupMessage = objectMapper.readValue(jsonStr, GroupMessage.class);
            UserInfo sender = groupMessage.getSender();
            String senderId = sender.getUserId();
            String receiveGroupId = groupMessage.getGroupId();
            log.info(groupMessage.toString());
            Set receivers = groupMessage.getReceiverIdSet();
            Set receiveTerminals = groupMessage.getReceiveTerminals();
            receivers.forEach((receiverId)->{
                receiveTerminals.forEach((terminal)->{
                    log.info("接收到消息，发送者:{},接收者:{}，内容:{}", senderId, receiverId, groupMessage.getData());
                    ChannelHandlerContext context = UserChannelCtxMap.getChannelCtx((String) receiverId,(Integer) terminal);
                    if(context!=null){
                        //接受者在线,推送消息给他
                        ReceivedGroupMsg<Object> receivedGroupMsg = new ReceivedGroupMsg<>(senderId,groupMessage.getData());
                        SendInfo<Object> sendInfo = new SendInfo<>();
                        sendInfo.setCmd(NettyCmdType.GROUP_MESSAGE.getCode());
                        sendInfo.setData(receivedGroupMsg);
                        context.channel().writeAndFlush(sendInfo);

                        /**
                         * 如果有将发送结果写回客户端的需要的话
                        if(groupMessage.getSendResult()){
                            SendInfo<Object> sendInfo1 = new SendInfo<>();
                            sendInfo1.setCmd(NettyCmdType.GROUP_MESSAGE.getCode());
                            sendInfo1.setData(SendResultEnum.SUCCESS.getCode());
                            //发送结果写回到发送者客户端
                            ctx.channel().writeAndFlush(sendInfo1);
                        }**/
                    }else{
                        log.info("未找到用户所在ChannelHandlerContext, 可能是用户不在线 发送者:"+sender.getUserId()
                                +", 接收者: "+receiverId+"接收者终端: "+terminal+ "消息内容: "+groupMessage.getData());
                        /* 思路:
                          1:只把消息id存放到redis中,未登录用户拉取消息时通过消息id从数据库来拉取未读消息
                          2:redis中仅保存一定数量的消息(最新的消息)如200条,超过这个数量的消息通过数据库保存
                          拉取时先拉取redis中的消息,如果redis中消息已经被全部拉取,再通过时间戳向数据库拉取
                         */
                        UnreadGroupMsg unreadGroupMsg = new UnreadGroupMsg();
                        unreadGroupMsg.setSender(new UserInfo(senderId, sender.getTerminal()));
                        unreadGroupMsg.setId(groupMessage.getId());
                        unreadGroupMsg.setData(groupMessage.getData());
                        unreadGroupMsg.setSendTime(groupMessage.getSendTime());
                        unreadGroupMsg.setStatus(MessageStatusEnum.UNREAD.getCode());
                        //未读消息的格式是 chat:message:group:unread:terminal:groupId:memberId
                        String key = ChatRedisKeys.CHAT_GROUP_MESSAGE_UNREAD +terminal+":"
                                +receiveGroupId+":"+receiverId;
                        //保存到redis, 放到未读消息队列
                        redisTemplate.opsForList().rightPush(key,unreadGroupMsg);
                        //保存到未读消息联系人集合
                        redisTemplate.opsForSet().add(ChatRedisKeys.CHAT_UNREAD_CONTACT_SET+receiverId,senderId);
                        //有取得发送结果的需求
                        if(groupMessage.getSendResult()){
                            SendInfo<Object> sendInfo = new SendInfo();
                            sendInfo.setCmd(NettyCmdType.GROUP_MESSAGE.getCode());
                            sendInfo.setData(SendResultEnum.RECEIVER_NOT_ONLINE.getCode());
                            //发送结果写回到客户端
                            ctx.channel().writeAndFlush(sendInfo);
                        }
                    }
                });
            });
        } catch (Exception e) {
            log.info(e.getMessage());
            SendInfo<Object> sendInfo = new SendInfo<>();
            sendInfo.setCmd(NettyCmdType.GROUP_MESSAGE.getCode());
            sendInfo.setData(SendResultEnum.FAIL.getCode());
            ctx.channel().writeAndFlush(sendInfo);
        }
    }
}
