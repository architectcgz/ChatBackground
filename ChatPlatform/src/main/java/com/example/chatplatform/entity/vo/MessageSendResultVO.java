package com.example.chatplatform.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author archi
 */
@Data
@AllArgsConstructor
public class MessageSendResultVO {
    private Long messageId;
    private Date sendTime;
}
