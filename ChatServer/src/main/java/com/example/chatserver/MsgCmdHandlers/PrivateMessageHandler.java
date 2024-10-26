package com.example.chatserver.MsgCmdHandlers;

import com.example.chatcommon.constants.ChatRedisKeys;
import com.example.chatcommon.enums.MessageStatusEnum;
import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.SendResultEnum;
import com.example.chatcommon.models.*;
import com.example.chatcommon.utils.JsonUtils;
import com.example.chatserver.UserChannelCtxMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@Slf4j
public class PrivateMessageHandler implements CommandHandler{
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public void handle(ChannelHandlerContext ctx, SendInfo msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(msg.getData());
            PrivateMessage privateMessage = objectMapper.readValue(jsonStr,PrivateMessage.class);
            String senderId = privateMessage.getSender().getUserId();
            String receiverId = privateMessage.getReceiverId();
            Set<Integer> receiveTerminals = privateMessage.getReceiveTerminals();
            log.info("接收到消息，发送者:{},接收者:{}，内容:{}", senderId, receiverId, privateMessage.getData());
            receiveTerminals.forEach((terminal)->{
                ChannelHandlerContext receiverCtx = UserChannelCtxMap.getChannelCtx(receiverId, terminal);
                //接受者在线,推送消息给他
                if(receiverCtx!=null){
                    SendInfo<Object> sendInfo = new SendInfo<>();
                    ReceivedPrivateMsg receivedPrivateMsg = new ReceivedPrivateMsg(
                            senderId,privateMessage.getData()
                    );
                    sendInfo.setCmd(NettyCmdType.PRIVATE_MESSAGE.getCode());
                    sendInfo.setData(receivedPrivateMsg);
                    log.info("receiver channel id: "+receiverCtx.channel().id().asShortText());
                    log.info("sender channel id: "+ctx.channel().id().asShortText());
                    receiverCtx.channel().writeAndFlush(sendInfo);

                    //有取得发送结果的需求
                    if(privateMessage.getSendResult()){
                        //异步写操作,要重新定义SendInfo对象，否则会将同一个sendInfo写回两个不同ctx
                        SendInfo sendInfo1 = new SendInfo(NettyCmdType.PRIVATE_MESSAGE.getCode(),SendResultEnum.SUCCESS.getCode());
                        //不能用上面的sendInfo
                        //sendInfo.setData(114);
                        //发送结果写回到客户端
                        ctx.channel().writeAndFlush(sendInfo1);
                    }
                }else{

                    log.info("未找到用户所在ChannelHandlerContext, 可能是用户不在线 发送者:"+senderId
                            +", 接收者: "+receiverId+"消息内容: "+privateMessage.getData());
                    /* 思路:
                      1:只把消息id存放到redis中,未登录用户拉取消息时通过消息id从数据库来拉取未读消息
                      2:redis中仅保存一定数量的消息(最新的消息)如200条,超过这个数量的消息通过数据库保存
                      拉取时先拉取redis中的消息,如果redis中消息已经被全部拉取,再通过时间戳向数据库拉取
                     */

                    UnreadPrivateMsg unreadPrivateMsg = new UnreadPrivateMsg();
                    unreadPrivateMsg.setId(privateMessage.getId());
                    unreadPrivateMsg.setData(privateMessage.getData());
                    unreadPrivateMsg.setStatus(MessageStatusEnum.UNREAD.getCode());
                    unreadPrivateMsg.setSendTime(privateMessage.getSendTime());

                    //未读消息的格式是 chat:message:private:unread:terminal:receiverId:senderId
                    String key = ChatRedisKeys.CHAT_PRIVATE_MESSAGE_UNREAD+ terminal+":"
                            + privateMessage.getReceiverId()+":"+ senderId;
                    //保存到redis, 放到未读消息队列
                    //redis中只保保存200条消息,多余的pop出去,保存到数据库
                    if(redisTemplate.opsForList().size(key)==200){
                        String value = (String) redisTemplate.opsForList().leftPop(key);
                        UnreadPrivateMsg oldMsg = JsonUtils.jsonStrToJavaObj(value,UnreadPrivateMsg.class);
                        //将旧消息写入到数据库

                        redisTemplate.opsForList().rightPush(key, unreadPrivateMsg);
                    }else{
                        redisTemplate.opsForList().rightPush(key, unreadPrivateMsg);
                    }

                    //保存到未读消息联系人集合
                    //redisTemplate.opsForSet().add(ChatRedisKeys.CHAT_UNREAD_CONTACT_SET+receiverId,senderId);
                    //有取得发送结果的需求
                    /*
                    if(privateMessage.getSendResult()){
                        SendInfo<Object> sendInfo = new SendInfo();
                        sendInfo.setCmd(NettyCmdType.PRIVATE_MESSAGE.getCode());
                        sendInfo.setData(SendResultEnum.RECEIVER_NOT_ONLINE.getCode());
                        //发送结果写回到客户端
                        ctx.channel().writeAndFlush(sendInfo);
                    }
                     */
                }
            });
        }catch (Exception e){
            SendInfo<Object> sendInfo = new SendInfo<>();
            sendInfo.setCmd(NettyCmdType.PRIVATE_MESSAGE.getCode());
            sendInfo.setData(SendResultEnum.FAIL.getCode());
            ctx.channel().writeAndFlush(sendInfo);
        }

    }

}
