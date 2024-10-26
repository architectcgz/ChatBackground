package com.example.chatcommon.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author archi
 */
@Data
public class Message implements Serializable {
    private static final Long serialVersionUID = 1L;
    private Long id;
    private String senderId;
    private String receiverId;
    private String content;
    private String mediaName;
    private String mediaUrl;
    private Long mediaSize;
    /**
     * 消息类型 0:文字 1:图片 2:文件 3:语音  4:撤回消息
     */
    private Integer messageType;
    private Integer status;
    private Date sendTime;
}
