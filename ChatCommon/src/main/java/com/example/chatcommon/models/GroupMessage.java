package com.example.chatcommon.models;

import com.example.chatcommon.enums.TerminalType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class GroupMessage<T> implements Serializable {
    private static final Long serialVersionUID = 1L;
    private Long id;
    /**
     * 发送方
     */
    private UserInfo sender;

    private String groupId;

    /**
     * 接收者id集合(群成员集合,为空则不会推送)
     */
    private Set<String> receiverIdSet = new HashSet<>();
    /**
     * 接收者终端类型,默认全部
     */
    private Set<Integer> receiveTerminals = TerminalType.codes();
    /**
     * 消息内容
     */
    private T data;

    private Boolean sendResult;

    public Date sendTime;
}
