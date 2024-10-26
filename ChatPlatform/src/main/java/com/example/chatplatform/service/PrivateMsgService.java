package com.example.chatplatform.service;

import com.example.chatplatform.entity.dto.MessageDTO;
import com.example.chatplatform.entity.vo.MessageSendResultVO;
import com.example.chatplatform.entity.vo.MessageVO;

import java.util.List;

public interface PrivateMsgService {

    MessageSendResultVO sendMessage(MessageDTO msg);

    void recallMessage(Long id);

    List<MessageVO> getUnreadList(String friendId,Long count);
}
