package com.example.chatcommon.models;

import lombok.Data;

import java.util.Date;

/**
 * @author archi
 */
@Data
public class UnreadPrivateMsg<T> {
    private Long id;
    private T data;
    private Date sendTime;
    private Integer status;
}
