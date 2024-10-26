package com.example.chatplatform.entity.dto;

import com.example.chatplatform.entity.constants.FormatMessage;
import jakarta.validation.constraints.NotEmpty;

public class UnreadMsgParam {
    /**
     * 要查找未读消息记录的好友的id
     */
    @NotEmpty(message = FormatMessage.FRIEND_ID_EMPTY_ERROR)
    private String friendId;
    /**
     * 要查找未读消息记录的数量
     */

    private Long count;
}
