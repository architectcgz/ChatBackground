package com.example.chatplatform.service;

import com.example.chatplatform.entity.dto.MessageDTO;

/**
 * @author archi
 */
public interface GroupMsgService {
    Long sendMessage(MessageDTO message);

    void recallMessage(Long messageId);
}
