package com.example.chatplatform.mapper;

import com.example.chatcommon.po.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PrivateMsgMapper {
    void saveMessage(Message message);
}
