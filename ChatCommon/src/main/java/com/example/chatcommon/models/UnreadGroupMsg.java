package com.example.chatcommon.models;

import lombok.Data;

import java.util.Date;

@Data
public class UnreadGroupMsg<T> {
    private Long id;
    private UserInfo sender;
    private T data;
    private Date sendTime;
    private Integer status;
}
