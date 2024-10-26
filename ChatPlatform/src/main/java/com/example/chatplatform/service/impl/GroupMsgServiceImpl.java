package com.example.chatplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.example.chatcommon.enums.NettyCmdType;
import com.example.chatcommon.enums.TerminalType;
import com.example.chatcommon.models.GroupMessage;
import com.example.chatcommon.models.SendInfo;
import com.example.chatcommon.models.UserInfo;
import com.example.chatcommon.po.Message;
import com.example.chatplatform.entity.CustomException;
import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.constants.SystemConstants;
import com.example.chatplatform.entity.dto.MessageDTO;
import com.example.chatplatform.entity.enums.MessageTypeEnum;
import com.example.chatplatform.entity.enums.ResponseEnum;
import com.example.chatplatform.entity.po.User;
import com.example.chatplatform.mapper.GroupMapper;
import com.example.chatplatform.mapper.MessageMapper;
import com.example.chatplatform.service.GroupMsgService;
import com.example.chatplatform.util.RedisUtils;
import com.example.chatplatform.util.RedissonUtil;
import com.example.chatplatform.util.SecurityContextUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author archi
 */
@Service
public class GroupMsgServiceImpl implements GroupMsgService {
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private RedissonUtil redissonUtil;
    @Override
    public Long sendMessage(MessageDTO message) {
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(message.getMessageType());
        if(messageTypeEnum==null){
            throw new CustomException(ResponseEnum.MESSAGE_TYPE_ERROR.getCode(),ResponseEnum.MESSAGE_TYPE_ERROR.getMessage());
        }
        User user = SecurityContextUtils.getUserNotNull();
        String userId = user.getUserId();
        String groupId = message.getReceiverId();
        //检查群是否存在
        String key = RedisKeys.USER_GROUPS+userId;
        Set<String> userGroupSet = RedisUtils.getZSet(key);
        if(userGroupSet==null){
            userGroupSet = groupMapper.getMyGroups(userId);
            RedisUtils.storeSet(key,userGroupSet, SystemConstants.REDIS_THREE_DAYS_EXPIRATION);
        }
        if(!userGroupSet.contains(groupId)){
            throw new CustomException(ResponseEnum.CANNOT_SEND_TO_GROUP_NOT_MEMBER.getCode(),ResponseEnum.CANNOT_SEND_TO_GROUP_NOT_MEMBER.getMessage());
        }
        //拿到群成员set
        Set<String> memberSet = RedisUtils.getZSet(RedisKeys.USER_GROUPS+groupId);
        if(memberSet == null){
            memberSet = groupMapper.getGroupMemberSet(groupId);
        }
        //不发送给自己
        memberSet.remove(userId);
        //保存到数据库
        Message msg = BeanUtil.copyProperties(message, Message.class);
        msg.setReceiverId(groupId);
        msg.setSendTime(DateTime.now());
        messageMapper.saveMessage(msg);
        //发送给群成员
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setId(msg.getId());
        groupMessage.setSender(new UserInfo(userId,user.getTerminal()));
        groupMessage.setGroupId(groupId);
        groupMessage.setSendResult(true);
        groupMessage.setReceiverIdSet(memberSet);
        groupMessage.setData(msg.getContent());
        groupMessage.setSendTime(DateTime.now());
        //默认发送给全平台
        groupMessage.setReceiveTerminals(TerminalType.codes());

        SendInfo sendInfo = new SendInfo(NettyCmdType.GROUP_MESSAGE.getCode(),groupMessage);
        redissonUtil.sendMessage(sendInfo);
        //检查消息是否发送成功
        return msg.getId();
    }

    @Override
    public void recallMessage(Long messageId) {

    }
}
