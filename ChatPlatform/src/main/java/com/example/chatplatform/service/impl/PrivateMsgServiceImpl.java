package com.example.chatplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.example.chatcommon.constants.ChatRedisKeys;
import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.TerminalType;
import com.example.chatcommon.models.PrivateMessage;
import com.example.chatcommon.models.SendInfo;
import com.example.chatcommon.models.UnreadPrivateMsg;
import com.example.chatcommon.models.UserInfo;
import com.example.chatcommon.po.Message;
import com.example.chatplatform.entity.CustomException;
import com.example.chatplatform.entity.dto.MessageDTO;
import com.example.chatplatform.entity.enums.MessageStatusEnum;
import com.example.chatplatform.entity.enums.MessageTypeEnum;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.entity.vo.MessageSendResultVO;
import com.example.chatplatform.entity.vo.MessageVO;
import com.example.chatplatform.mapper.MessageMapper;
import com.example.chatplatform.service.FriendService;
import com.example.chatplatform.service.PrivateMsgService;
import com.example.chatplatform.util.RedissonUtil;
import com.example.chatplatform.util.SecurityContextUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author archi
 */
@Slf4j
@Service
public class PrivateMsgServiceImpl implements PrivateMsgService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private FriendService friendService;
    @Resource
    private RedissonUtil redissonUtil;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public MessageSendResultVO sendMessage(MessageDTO msg) {
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(msg.getMessageType());
        if(messageTypeEnum==null){
            throw new CustomException(ResponseEnum.MESSAGE_TYPE_ERROR.getCode(),ResponseEnum.MESSAGE_TYPE_ERROR.getMessage());
        }
        User user = SecurityContextUtils.getUserNotNull();
        String userId = user.getUserId();
        String receiveUserId = msg.getReceiverId();

        //不是对方的好友，不能向对方发消息
        boolean isFriend = friendService.isFriend(userId,receiveUserId);
        //如果被对方拉黑，不能向对方发消息
        boolean isBlocked = friendService.isBlocked(userId,receiveUserId);
        if(!isFriend||isBlocked){
            throw new CustomException(ResponseEnum.CANNOT_SEND_MSG.getCode(), ResponseEnum.CANNOT_SEND_MSG.getMessage());
        }
        //保存信息
        Message message = BeanUtil.copyProperties(msg, Message.class);
        message.setSenderId(userId);
        //如果是文件,先设置成发送中,文件上传到服务器后再设置成已发送
        if(messageTypeEnum.equals(MessageTypeEnum.TEXT)){
            message.setStatus(MessageStatusEnum.UNSENT.getCode());
        }else{
            message.setStatus(MessageStatusEnum.SENDING.getCode());
        }
        Date sendTime = DateTime.now().toTimestamp();
        message.setSendTime(sendTime);

        try {
            //--- 使用Redisson 转发消息的部分 ---
            PrivateMessage<Message> privateMessage = new PrivateMessage<>();
            privateMessage.setId(message.getId());
            privateMessage.setSender(new UserInfo(userId,user.getTerminal()));
            privateMessage.setReceiverId(message.getReceiverId());
            privateMessage.setData(message);
            privateMessage.setSendResult(false);
            privateMessage.setSendTime(sendTime);
            SendInfo sendInfo = new SendInfo<>();
            sendInfo.setCmd(NettyCmdType.PRIVATE_MESSAGE.getCode());
            sendInfo.setData(privateMessage);
            redissonUtil.sendMessage(sendInfo);
            //--- 使用Redisson 转发消息的部分 ---
        }catch (Exception e){
            //发送消息出现问题
            log.info("发送消息失败");
            e.printStackTrace();
            //TODO 发送消息失败的处理
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误,消息发送失败");
        }
        //消息转发成功后,保存到数据库,并且拿到这条信息的id
        messageMapper.saveMessage(message);
        log.info("发送私聊消息，发送id:{},接收id:{}，内容:{}", userId,receiveUserId, msg.getContent());
        return new MessageSendResultVO(message.getId(),sendTime);
    }

    @Override
    public void recallMessage(Long id) {
        Message message = messageMapper.getMsgById(id);
        if(message==null){
            throw new CustomException(ResponseEnum.MSG_NOT_EXIST_ERROR.getCode(), ResponseEnum.MSG_NOT_EXIST_ERROR.getMessage());
        }
        User user = SecurityContextUtils.getUserNotNull();
        if(!user.getUserId().equals(message.getSenderId())){
            throw new CustomException(ResponseEnum.MSG_SENDER_ERROR.getCode(), ResponseEnum.MSG_SENDER_ERROR.getMessage());
        }
        message.setStatus(MessageStatusEnum.RECALL.getCode());
        messageMapper.updateById(id,message);
        //推送消息
        MessageVO msg = BeanUtil.copyProperties(message,MessageVO.class);
        msg.setSendTime(DateTime.now());
        msg.setContent("对方撤回了一条消息");
        msg.setType(MessageStatusEnum.RECALL.getCode());
        PrivateMessage<MessageVO> privateMessage = new PrivateMessage<>();
        privateMessage.setSender(new UserInfo(msg.getSenderId(), user.getTerminal()));
        privateMessage.setReceiverId(msg.getReceiverId());
        privateMessage.setSendToSelf(false);
        privateMessage.setData(msg);
        privateMessage.setSendTime(DateTime.now());
        privateMessage.setSendResult(false);

        SendInfo sendInfo = new SendInfo(NettyCmdType.PRIVATE_MESSAGE.getCode(), privateMessage);
        redissonUtil.sendMessage(sendInfo);
        //推送到自己其他终端
        msg.setContent("你撤回了一条消息");
        privateMessage.setId(msg.getId());
        privateMessage.setData(msg);
        privateMessage.setReceiverId(user.getUserId());
        privateMessage.setSendToSelf(true);
        privateMessage.setReceiveTerminals(TerminalType.codes());
        privateMessage.setSendTime(DateTime.now());
        sendInfo.setData(privateMessage);
        redissonUtil.sendMessage(sendInfo);
        log.info("撤回私聊消息，发送id:{},接收id:{}，内容:{}", message.getSenderId(), msg.getReceiverId(), msg.getContent());
    }

    @Override
    public List<MessageVO> getUnreadList(String friendId,Long count) {
        User user = SecurityContextUtils.getUserNotNull();
        String key = ChatRedisKeys.CHAT_PRIVATE_MESSAGE_UNREAD+user.getTerminal()+":" + user.getUserId()+":" + friendId;
        //没这个key,说明没有未读消息列表
        if(Boolean.FALSE.equals(redisTemplate.hasKey(key))){
            log.info("Redis中无key:{}",key);
            return new ArrayList<>();
        }
        long c = 0L;
        List<MessageVO> messageList = new ArrayList<>();
        //count过大,则替换为实际未读消息列表大小
        if(count>redisTemplate.opsForList().size(key)){
            count = redisTemplate.opsForList().size(key);
        }
        log.info(key);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            while (c < count){
                String json = objectMapper.writeValueAsString(redisTemplate.opsForList().rightPop(key)) ;
                UnreadPrivateMsg<MessageVO> unreadPrivateMsg = objectMapper.readValue(json, UnreadPrivateMsg.class);
                String messageVOJson = objectMapper.writeValueAsString(unreadPrivateMsg.getData());
                log.info(messageVOJson);
                messageList.add(objectMapper.readValue(messageVOJson, MessageVO.class));
                c++;
            }
        }catch (Exception e){
            log.info(e.getMessage());
            throw new CustomException(ResponseEnum.INTERNAL_ERROR.getCode(),ResponseEnum.INTERNAL_ERROR.getMessage());
        }
        return messageList;
    }

}
