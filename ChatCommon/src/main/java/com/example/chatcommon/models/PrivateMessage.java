package com.example.chatcommon.models;

import com.example.chatcommon.enums.TerminalType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
public class PrivateMessage<T> implements Serializable {
    private static final Long serialVersionUID = 1L;
    private Long id;

    /**
     * 发送方
     */
    private UserInfo sender;

    /**
     * 接收者id
     */
    private String receiverId;


    /**
     * 接收者终端类型,默认全部
     */
    private Set<Integer> receiveTerminals = TerminalType.codes();

    /**
     * 消息内容
     */
    private T data;

    /**
     * 是否推送到自己其他终端
     */
    private Boolean sendToSelf;
    private Date sendTime;
    private Boolean sendResult;
}
